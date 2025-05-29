import tippy from "tippy.js";

function xmlEscape(str) {
  return str.replace(/[<>&'"]/g, (match) => {
    switch (match) {
      case "<":
        return "&lt;";
      case ">":
        return "&gt;";
      case "&":
        return "&amp;";
      case "'":
        return "&apos;";
      case '"':
        return "&quot;";
    }
  });
}

function generateSVGIcon(name) {
    const icons = document.getElementById("custom-header-icons");
    return icons.content.querySelector(`#${name}`).cloneNode(true);
}

function createElementFromHtml(html) {
  const template = document.createElement("template");
  template.innerHTML = html.trim();
  return template.content.firstElementChild;
};

function menuItem(options) {
  let item = null;
  if (options.type === "separator") {
    if (options.title) {
      item = createElementFromHtml(`
        <div class="jenkins-dropdown__heading">${xmlEscape(options.title)}</div>
      `);
    } else {
      item = separator();
    }
  } else {
    const label = xmlEscape(options.label);
    let linkIcon = "";
    if (options.external) {
      linkIcon = generateSVGIcon("external-link").outerHTML;
    }
    let color = "";
    if (options.color) {
      color = options.color;
    }
    item = createElementFromHtml(`
      <a class="jenkins-dropdown__item" href="${options.linkUrl}" ${options.external? `target="_blank"` : ``}>
        ${
          options.iconUrl
            ? `<div class="jenkins-dropdown__item__icon ${color}">${
                options.iconXml
                  ? options.iconXml
                  : `<img src="${options.iconUrl}" class="custom-header__link-image"/>`
              }</div>`
            : ``
        }
        ${label} ${linkIcon}
      </a>
    `);
  }
  return item;
};

function separator() {
  return createElementFromHtml(
    `<div class="jenkins-dropdown__separator"></div>`,
  );
}

function generateItems(links) {
  const menuItems = document.createElement("div");
  menuItems.classList.add("jenkins-dropdown");
  links.map((item) => {
    return menuItem(item);
  })
  .forEach((item) => menuItems.appendChild(item));

  return menuItems;
}

function callback(element, instance) {
  const href = element.dataset.href;

  fetch(href).then((response) => response.json()).then(json => {
    instance.setContent(generateItems(json.links));
  })
  .catch((error) => console.log(`AppNav request failed: ${error}`))
  .finally(() => (instance.loaded = true));
}

// Function to check app-nav visibility
function checkAppNavVisibility() {
  const appNavButton = document.querySelector(".custom-header__app-nav-button");
  
  console.log("DEBUG: checkAppNavVisibility called");
  
  if (!appNavButton) {
    console.log("DEBUG: appNavButton not found in DOM");
    return; // Button doesn't exist, nothing to do
  }
  
  // Get the Jenkins root URL from the page context or calculate it
  const rootURL = (window.rootURL || '') || 
                  (document.querySelector('head base')?.getAttribute('href')?.replace(/\/$/, '') || '');
  
  console.log("DEBUG: Fetching favorite status from:", `${rootURL}/customizable-header/favoriteStatusChanged`);
  
  // Fetch the current status from the endpoint
  fetch(`${rootURL}/customizable-header/favoriteStatusChanged`)
    .then((response) => response.json())
    .then((data) => {
      console.log("DEBUG: Got favorite status response:", data);
      if (data.showAppNav) {
        appNavButton.style.display = ""; // Show the button
        console.log("DEBUG: Showing app-nav button");
      } else {
        appNavButton.style.display = "none"; // Hide the button
        console.log("DEBUG: Hiding app-nav button");
      }
    })
    .catch((error) => console.error("Error checking app-nav visibility:", error));
}

// Set up favorite plugin event listeners
function setupFavoriteEventListeners() {
  console.log("DEBUG: Setting up favorite event listeners");
  
  // Listen for favorite changes using a custom event
  // This will be triggered by a MutationObserver watching for DOM changes from the favorite plugin
  document.addEventListener("jenkins:favorite-changed", () => {
    console.log("DEBUG: jenkins:favorite-changed event received");
    checkAppNavVisibility();
  });
  
  // Try to find favorite icons with alternative selectors
  function findAllFavoriteIcons() {
    // Different Jenkins versions and themes might use different classes for favorite icons
    const selectors = [
      ".icon-favorite", 
      ".icon-favorite-inactive", 
      ".favorite-icon",
      ".fav-button",
      "svg.icon-favorite", 
      "svg.icon-fav",
      ".favorite-toggle",
      "[data-favorite]",
      "a.favorite"
    ];
    
    let allIcons = [];
    selectors.forEach(selector => {
      const icons = document.querySelectorAll(selector);
      console.log(`DEBUG: Found ${icons.length} icons with selector "${selector}"`);
      allIcons = [...allIcons, ...icons];
    });
    
    return allIcons;
  }
  
  // Directly add click handler to favorite icons
  function addClickHandlers() {
    const allFavoriteButtons = findAllFavoriteIcons();
    console.log("DEBUG: Adding click handlers to", allFavoriteButtons.length, "favorite buttons");
    
    allFavoriteButtons.forEach(button => {
      // Remove existing handlers first to avoid duplicates
      button.removeEventListener("click", favoriteClickHandler);
      button.addEventListener("click", favoriteClickHandler);
    });
  }
  
  function favoriteClickHandler(event) {
    console.log("DEBUG: Favorite icon clicked", event.target);
    // Give the server a moment to process the favorite change
    setTimeout(() => {
      console.log("DEBUG: Dispatching favorite-changed event after click");
      document.dispatchEvent(new CustomEvent("jenkins:favorite-changed"));
    }, 500);
  }
  
  // Set up a more comprehensive MutationObserver
  const observer = new MutationObserver((mutations) => {
    console.log("DEBUG: MutationObserver triggered, mutations:", mutations.length);
    
    let favoriteChanged = false;
    
    mutations.forEach((mutation) => {
      // Check if this is a class change on an element that might be a favorite button
      if (mutation.type === "attributes" && mutation.attributeName === "class") {
        const target = mutation.target;
        const classList = target.classList ? Array.from(target.classList) : [];
        
        console.log("DEBUG: Class mutation on", target, "classList:", classList);
        
        // Check for any class that might indicate a favorite icon
        if (classList.some(cls => cls.includes("fav") || cls.includes("favorite"))) {
          console.log("DEBUG: Potential favorite icon class change detected");
          favoriteChanged = true;
        }
      }
      
      // Check for added/removed nodes that might be favorite icons
      if (mutation.type === "childList") {
        // Check added nodes
        mutation.addedNodes.forEach(node => {
          if (node.nodeType === Node.ELEMENT_NODE) {
            const hasFavoriteClass = node.classList && 
                Array.from(node.classList).some(cls => cls.includes("fav") || cls.includes("favorite"));
            
            if (hasFavoriteClass) {
              console.log("DEBUG: Favorite icon added to DOM", node);
              favoriteChanged = true;
            }
            
            // Check for favorite icons inside the added node
            const favIcons = node.querySelectorAll('[class*="fav"], [class*="favorite"]');
            if (favIcons.length > 0) {
              console.log("DEBUG: Found favorite icons inside added node", favIcons);
              favoriteChanged = true;
            }
          }
        });
      }
    });
    
    if (favoriteChanged) {
      console.log("DEBUG: Favorite change detected, dispatching event");
      document.dispatchEvent(new CustomEvent("jenkins:favorite-changed"));
      
      // Re-add click handlers to any new favorite buttons
      addClickHandlers();
    }
  });
  
  // Start observing for changes
  window.setTimeout(() => {
    // First, set up click handlers
    addClickHandlers();
    
    // Observe the entire document for any changes that might affect favorites
    observer.observe(document.body, { 
      childList: true, 
      subtree: true,
      attributes: true,
      attributeFilter: ["class"]
    });
    
    console.log("DEBUG: Comprehensive observer setup complete");
    
    // Also add a click listener to the document to catch any favorite button clicks
    document.addEventListener("click", function(event) {
      // Check if the clicked element or any of its parents is a favorite icon
      let target = event.target;
      while (target && target !== document) {
        if (target.classList && 
            Array.from(target.classList).some(cls => cls.includes("fav") || cls.includes("favorite"))) {
          console.log("DEBUG: Favorite icon or container clicked via document listener", target);
          setTimeout(() => checkAppNavVisibility(), 500);
          break;
        }
        target = target.parentNode;
      }
    });
  }, 1000); // Short delay to ensure DOM is ready
}

function init() {
  Behaviour.specify(".custom-header__app-nav-button", "app-nav", 0, function (element) {
    tippy(
      element,
      {
        content: "<p class='jenkins-spinner'></p>",
        interactive: true,
        trigger: "click",
        allowHTML: true,
        appendTo: document.getElementById("page-header"),
        placement: "bottom-start",
        arrow: false,
        theme: "dropdown",
        offset: [0, 0],
        animation: "dropdown",
        onCreate(instance) {
          instance.reference.addEventListener("click", () => {
            callback(element, instance);
            if (instance.loaded) {
              return;
            }

            instance.popper.addEventListener("click", () => {
              instance.hide();
            });
          });
        },
        onShown(instance) {
          Behaviour.applySubtree(instance.popper);
        },
      }
    )
  });
  
  // Check app-nav visibility initially
  checkAppNavVisibility();
  
  // Set up favorite event listeners
  setupFavoriteEventListeners();
  
  // Periodically check for app-nav visibility (as a fallback)
  window.setInterval(checkAppNavVisibility, 5000);
}

export default { init };

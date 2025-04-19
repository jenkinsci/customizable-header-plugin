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
  
  if (!appNavButton) {
    return; // Button doesn't exist, nothing to do
  }
  
  // Get the Jenkins root URL from the page context or calculate it
  const rootURL = (window.rootURL || '') || 
                  (document.querySelector('head base')?.getAttribute('href')?.replace(/\/$/, '') || '');
  
  // Fetch the current status from the endpoint
  fetch(`${rootURL}/customizable-header/favoriteStatusChanged`)
    .then((response) => response.json())
    .then((data) => {
      if (data.showAppNav) {
        appNavButton.style.display = ""; // Show the button
      } else {
        appNavButton.style.display = "none"; // Hide the button
      }
    })
    .catch((error) => console.error("Error checking app-nav visibility:", error));
}

// Set up favorite plugin event listeners
function setupFavoriteEventListeners() {
  // Listen for favorite changes using a custom event
  // This will be triggered by a MutationObserver watching for DOM changes from the favorite plugin
  document.addEventListener("jenkins:favorite-changed", () => {
    checkAppNavVisibility();
  });
  
  // Set up a MutationObserver to detect when favorites are added/removed
  const observer = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
      if (mutation.target.classList && 
          (mutation.target.classList.contains("icon-favorite") || 
           mutation.target.classList.contains("icon-favorite-inactive"))) {
        // Dispatch custom event when a favorite icon changes
        document.dispatchEvent(new CustomEvent("jenkins:favorite-changed"));
      }
    });
  });
  
  // Start observing favorite icons for changes
  window.setTimeout(() => {
    const favoriteIcons = document.querySelectorAll(".icon-favorite, .icon-favorite-inactive");
    favoriteIcons.forEach((icon) => {
      observer.observe(icon, { attributes: true, attributeFilter: ["class"] });
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

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
  const label = xmlEscape(options.label);
  let linkIcon = "";
  if (options.external) {
    linkIcon = generateSVGIcon("external-link").outerHTML;
  }
  let color = "";
  if (options.color) {
    color = options.color;
  }
  const item = createElementFromHtml(`
    <a class="jenkins-dropdown__item" href="${options.url}" ${options.external? `target="_blank"` : ``}>
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
  `)
  return item;
};

function generateItems(links, favorites) {
  const menuItems = document.createElement("div");
  menuItems.classList.add("jenkins-dropdown");
  links.map((item) => {
    return menuItem(item);
  })
  .forEach((item) => menuItems.appendChild(item));
  if (favorites.length != 0 && links.length != 0) {
    const separator = createElementFromHtml(
      `<div class="jenkins-dropdown__separator"></div>`,
    );
    menuItems.appendChild(separator);
  }
  favorites.map((item) => {
    return menuItem(item);
  })
  .forEach((item) => menuItems.appendChild(item));

  return menuItems;
}

function callback(element, instance) {
  const href = element.dataset.href;

  fetch(href).then((response) => response.json()).then(json => {
    instance.setContent(generateItems(json.links, json.favorites));
  })
  .catch((error) => console.log(`AppNav request failed: ${error}`))
  .finally(() => (instance.loaded = true));
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
}

export default { init };

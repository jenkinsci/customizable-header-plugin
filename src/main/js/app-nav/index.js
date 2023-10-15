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

function createElementFromHtml(html) {
  const template = document.createElement("template");
  template.innerHTML = html.trim();
  return template.content.firstElementChild;
};

function menuItem(options) {
  const label = xmlEscape(options.label);
  const item = createElementFromHtml(`
    <a class="jenkins-dropdown__item" href="${options.url}" target="_blank">
      ${
        options.iconUrl
          ? `<div class="jenkins-dropdown__item__icon">${
              options.iconXml
                ? options.iconXml
                : `<img alt="${label}" src="${options.iconUrl}" />`
            }</div>`
          : ``
      }
      ${label}
    </a>
  `)
  return item;
};

function generateItems(items) {
  const menuItems = document.createElement("div");
  items.map((item) => {
    return menuItem(item);
  })
  .forEach((item) => menuItems.appendChild(item));

  return menuItems;
}

function callback(element, instance) {
  const href = element.dataset.href;

  fetch(href)
    .then((response) => response.json())
    .then((json) =>
      instance.setContent(
        generateItems(json.items)
      )
    )
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
          instance.reference.addEventListener("mouseenter", () => {
            if (instance.loaded) {
              return;
            }

            instance.popper.addEventListener("click", () => {
              instance.hide();
            });

            callback(element, instance);
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


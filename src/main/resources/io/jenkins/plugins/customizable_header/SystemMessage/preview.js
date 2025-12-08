Behaviour.specify(".ch-sm--preview-button", "custom-header-system-message-preview", 0, function (button) {
  button.addEventListener("click", function () {
    const message = button.closest(".ch-sm--config");
    const hideButton = message.querySelector(".ch-sm--preview-hide-button");
    hideButton.classList.remove("jenkins-hidden");
    showPreview(message, true, true);

  });
});

Behaviour.specify(".ch-sm--preview-hide-button", "custom-header-system-message-preview", 0, function (button) {
  button.addEventListener("click", function () {
    const message = button.closest(".ch-sm--config");
    const hideButton = message.querySelector(".ch-sm--preview-hide-button");
    hideButton.classList.add("jenkins-hidden");
    showPreview(message, true, false);
  });
});

Behaviour.specify(".ch-sm--config .ch-level-radio", "custom-header-system-message-preview", 0, function (radio) {
  radio.addEventListener("change", function () {
    const message = radio.closest(".ch-sm--config");
    showPreview(message, false);
  });
});

Behaviour.specify(".ch-sm--config .ch-include-symbol", "custom-header-system-message-preview", 0, function (checkbox) {
  checkbox.addEventListener("change", function () {
    const message = checkbox.closest(".ch-sm--config");
    showPreview(message, false);
  });
});

function showPreview(message, toggle, visible) {
  const container = message.querySelector(".ch-sm--preview-container");
  if (toggle) {
    if (visible === true) {
      container.classList.remove("jenkins-hidden");
    } else {
      container.classList.add("jenkins-hidden");
    }
  }
  if (!container.classList.contains("jenkins-hidden")) {
    const selectedLevel = message.querySelector('.ch-level-radio:checked').value;
    const includeSymbol = message.querySelector('input[name="_.includeSymbol"]').checked;
    container.classList.remove("jenkins-alert-danger", "jenkins-alert-warning", "jenkins-alert-info", "jenkins-alert-success");
    container.classList.add(`jenkins-alert-${selectedLevel}`);
    container.querySelectorAll("svg.ch-symbol").forEach(svg => svg.classList.add("jenkins-hidden"));
    if (includeSymbol) {
      if (selectedLevel === "danger") {
        const errorSysmbol = container.querySelector("svg.ch-symbol-alert");
        errorSysmbol.classList.remove("jenkins-hidden");
      }
      if (selectedLevel === "warning") {
        const errorSysmbol = container.querySelector("svg.ch-symbol-warning");
        errorSysmbol.classList.remove("jenkins-hidden");
      }
      if (selectedLevel === "info" || selectedLevel === "success") {
        const errorSysmbol = container.querySelector("svg.ch-symbol-info");
        errorSysmbol.classList.remove("jenkins-hidden");
      }
    }
    const messageDiv = message.querySelector('.ch-message');
    let textarea = message.getElementsByTagName("textarea")[0];
    const messagePreviewDiv = container.querySelector('.ch-sm--preview-text');
    let text = "";
    if (textarea == null) {
      textarea = messageDiv.getElementsByClassName("jenkins-readonly")[0];
      text = textarea != null ? textarea.innerText : "";
    } else {
      text = textarea.codemirrorObject
        ? textarea.codemirrorObject.getValue()
        : textarea.value;
    }

    fetch(rootURL + message.dataset.previewUrl, {
      method: "post",
      headers: crumb.wrap({
        "Content-Type": "application/x-www-form-urlencoded",
      }),
      body: new URLSearchParams({
        text: text,
      }),
    }).then((rsp) => {
      rsp.text().then((responseText) => {
        if (rsp.ok) {
          messagePreviewDiv.innerHTML = responseText;
        } else {
          messagePreviewDiv.innerHTML = rsp.status + " " + rsp.statusText + "<HR/>" + responseText;
        }
        return false;
      });
    });

  }
}

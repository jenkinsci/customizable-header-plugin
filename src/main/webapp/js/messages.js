Behaviour.specify(".ch-dismiss-link", "dismiss-link", 0, function (element) {
  element.onclick = function(event) {
    event.preventDefault();
    const uid = element.dataset.uid;
    const url = document.head.dataset.rooturl + "/customizable-header/dismissMessage?";
    const params = new URLSearchParams({ uid: uid});
    fetch(url + params, {
      method: "post",
      headers: crumb.wrap({}),
    });
    element.parentElement.remove();
  }
});
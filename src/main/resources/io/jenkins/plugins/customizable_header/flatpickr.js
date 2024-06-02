Behaviour.specify(".custom-header__system-message--expireDate", "ch-flatpickr", 0, function(fp) {
  flatpickr(fp, {
    allowInput: true,
    enableTime: true,
    wrap: true,
    clickOpens: false,
    dateFormat: "Y-m-d H:i",
    time_24hr: true,
    static: true,
    minDate: fp.dataset.now,
  });
});

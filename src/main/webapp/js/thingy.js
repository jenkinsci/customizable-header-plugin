document.querySelectorAll(".ch-sample").forEach(sample => {
    sample.addEventListener("click", () => {
        const backgroundColorInput = document.querySelector("[name='_.backgroundColor']");
        const colorInput = document.querySelector("[name='_.color']");

        backgroundColorInput.value = getComputedStyle(sample).getPropertyValue('background')
        colorInput.value = getComputedStyle(sample).getPropertyValue('color')
    })
})
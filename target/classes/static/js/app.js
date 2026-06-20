document.querySelectorAll(".delete-form").forEach((form) => {
    form.addEventListener("submit", (event) => {
        const confirmed = window.confirm("Delete this expense?");
        if (!confirmed) {
            event.preventDefault();
        }
    });
});

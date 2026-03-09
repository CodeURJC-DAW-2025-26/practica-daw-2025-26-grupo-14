document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".stat-bar").forEach((el) => {
    el.style.width = (el.dataset.width || 0) + "%";
  });

  const ctx = document.getElementById("productsByDateChart");
    if (!ctx || typeof Chart === "undefined") return;

    const labels = JSON.parse(ctx.dataset.labels || "[]");
    const data = JSON.parse(ctx.dataset.values || "[]");

    if (!labels.length || !data.length) return;

    new Chart(ctx, {
    type: "line",
    data: {
        labels,
        datasets: [{
        label: "Products created",
        data,
        borderColor: "#0d6efd",
        backgroundColor: "rgba(13,110,253,0.15)",
        fill: true,
        tension: 0.25
        }]
    },
    options: {
        responsive: true,
        scales: { y: { beginAtZero: true, ticks: { precision: 0 } } }
    }
    });

});

/*document.addEventListener("DOMContentLoaded", () => {
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

});*/

function drawLineChart(canvasId, label, color) {
  const ctx = document.getElementById(canvasId);
  if (!ctx || typeof Chart === "undefined") return;

  const labels = JSON.parse(ctx.dataset.labels || "[]");
  const data = JSON.parse(ctx.dataset.values || "[]");
  if (!labels.length || !data.length) return;

  new Chart(ctx, {
    type: "line",
    data: {
      labels,
      datasets: [{
        label,
        data,
        borderColor: color,
        backgroundColor: color.replace("1)", "0.15)"),
        fill: true,
        tension: 0.25
      }]
    },
    options: { responsive: true, scales: { y: { beginAtZero: true, ticks: { precision: 0 } } } }
  });
}

function drawPieChart(canvasId, label, colors) {
  const ctx = document.getElementById(canvasId);
  if (!ctx || typeof Chart === "undefined") return;

  const labels = JSON.parse(ctx.dataset.labels || "[]");
  const data = JSON.parse(ctx.dataset.values || "[]");
  if (!labels.length || !data.length) return;

  labels.reverse();
  data.reverse();

  new Chart(ctx, {
    type: "pie",
    data: {
      labels,
      datasets: [{
        label,
        data,
        backgroundColor: colors,
        borderColor: "#ffffff",
        borderWidth: 1
      }]
    },
    options: {
      responsive: true,
      plugins: { legend: { position: "bottom" } }
    }
  });
}

document.addEventListener("DOMContentLoaded", () => {
  drawLineChart("productsByDateChart", "Products created", "rgba(13,110,253,1)");
  drawLineChart("usersByDateChart", "Users created", "rgba(25,135,84,1)");
  drawPieChart("usersRatingsChart", "User ratings", [
    "#0d6efd", "#198754", "#ffc107", "#fd7e14", "#dc3545"
  ]);
});

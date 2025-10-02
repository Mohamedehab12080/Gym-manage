// Offers-specific JavaScript functionality

// Global variables
let allOffers = [];
let currentEditingOfferId = null;

// DOM Content Loaded
document.addEventListener("DOMContentLoaded", function () {
  loadOffers();
});

// Load offers from server
async function loadOffers() {
  try {
    showLoading(true);
    const response = await fetch("/api/offers");

    if (!response.ok) {
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }

    allOffers = await response.json();
    renderOffersTable(allOffers);
    updateAllStats(); // Changed from updateStats() to updateAllStats()
    showNotification("success", "تم بنجاح", "تم تحميل العروض بنجاح");
  } catch (error) {
    console.error("Error loading offers:", error);
    document.getElementById("offersTableBody").innerHTML =
      '<tr><td colspan="9" class="error-message">خطأ في تحميل العروض. يرجى التحقق من وحدة التحكم للتفاصيل.</td></tr>';
    showNotification(
      "error",
      "خطأ",
      "فشل في تحميل العروض. يرجى المحاولة مرة أخرى."
    );
  } finally {
    showLoading(false);
  }
}

// Enhanced statistics function that handles both offers and members
function updateAllStats() {
  if (!allOffers || allOffers.length === 0) {
    // Reset stats if no offers
    document.getElementById("totalOffers").textContent = "0";
    document.getElementById("activeOffers").textContent = "0";
    document.getElementById("totalMembers").textContent = "0";
    document.getElementById("totalActiveMembers").textContent = "0";
    document.getElementById("totalNotActive").textContent = "0";
    document.getElementById("totalMony").textContent = "$0";
    return;
  }

  // Calculate statistics in a single pass
  let activeOffersCount = 0;
  let totalMembersCount = 0;
  let activeMembersCount = 0;
  let notActiveMembersCount = 0;
  let totalMony = 0;

  allOffers.forEach((offer) => {
    // Count active offers
    if (offer.status === "ACTIVE") {
      activeOffersCount++;
    }
    // Sum member statistics (with null safety)
    totalMembersCount += offer.numberOfMembers || 0;
    activeMembersCount += offer.numberOfActiveMembers || 0;
    notActiveMembersCount += offer.numberOfNotActiveMembers || 0;
    totalMony += (offer.price || 0) * (offer.numberOfActiveMembers || 0);
  });

  // Update DOM elements
  document.getElementById("totalOffers").textContent = allOffers.length;
  document.getElementById("activeOffers").textContent = activeOffersCount;
  document.getElementById("totalMembers").textContent = totalMembersCount;
  document.getElementById("totalActiveMembers").textContent =
    activeMembersCount;
  document.getElementById("totalNotActive").textContent = notActiveMembersCount;
  document.getElementById("totalMony").textContent =
    "$" + totalMony.toLocaleString();
}

// Keep your original updateStats function if needed elsewhere, or remove it
function updateStats() {
  // This is your original function - you can keep it or replace all calls with updateAllStats()
  document.getElementById("totalOffers").textContent = allOffers.length;
  const activeCount = allOffers.filter(
    (offer) => offer.status === "ACTIVE"
  ).length;
  document.getElementById("activeOffers").textContent = activeCount;
}

// Render offers table
function renderOffersTable(offers) {
  const tableBody = document.getElementById("offersTableBody");

  if (!offers || offers.length === 0) {
    tableBody.innerHTML =
      '<tr><td colspan="9" style="text-align: center; padding: 20px;">No offers found</td></tr>';
    return;
  }

  // Clear existing content
  tableBody.innerHTML = "";

  // Format currency function
  const formatCurrency = (amount) => {
    return `$${amount ? parseFloat(amount).toFixed(2) : "0.00"}`;
  };

  // Calculate total money
  const calculateTotal = (members, price) => {
    return formatCurrency((members || 0) * (price || 0));
  };

  // Create rows for each offer
  offers.forEach((offer) => {
    const row = document.createElement("tr");

    // Add data attributes for potential filtering/sorting
    row.setAttribute("data-offer-id", offer.id);
    row.setAttribute("data-status", offer.status || "INACTIVE");

    row.innerHTML = `
        <td>${offer.id || "N/A"}</td>
        <td>${offer.title || "N/A"}</td>
        <td>${offer.numberOfSessions || 0}</td>
        <td>${offer.numberOfMonths || 0}</td>
        <td>${offer.numberOfActiveMembers || 0}</td>
        <td>${formatCurrency(offer.price)}</td>
        <td>${calculateTotal(offer.numberOfActiveMembers, offer.price)}</td>
        <td>
          <span class="status-badge ${
            offer.status === "ACTIVE" ? "status-active" : "status-inactive"
          }">
                        ${offer.status === "ACTIVE" ? "نشط" : "غير نشط"}
          </span>
        </td>
        <td class="actions-cell">
           <button class="btn btn-edit" onclick="editOffer(${
             offer.id
           })" title="تعديل العرض">
                        <i class="fas fa-edit"></i>
                    </button>
            <button class="btn btn-danger" onclick="deleteOffer(${
              offer.id
            })" title="حذف العرض">
                        <i class="fas fa-trash"></i>
                    </button>
        </td>
      `;

    tableBody.appendChild(row);
  });
}

// Filter offers
function filterOffers() {
  const filterValue = document.getElementById("statusFilter").value;

  let filteredOffers = allOffers;
  if (filterValue === "ACTIVE") {
    filteredOffers = allOffers.filter((offer) => offer.status === "ACTIVE");
  } else if (filterValue === "INACTIVE") {
    filteredOffers = allOffers.filter((offer) => offer.status === "INACTIVE");
  }

  renderOffersTable(filteredOffers);
}

// Search offers
function searchOffers() {
  const searchValue = document
    .getElementById("searchInput")
    .value.toLowerCase();

  if (!searchValue) {
    renderOffersTable(allOffers);
    return;
  }

  const filteredOffers = allOffers.filter(
    (offer) =>
      (offer.title && offer.title.toLowerCase().includes(searchValue)) ||
      (offer.id && offer.id.toString().includes(searchValue))
  );

  renderOffersTable(filteredOffers);
}

// Open create offer modal
function openCreateOfferModal(offer = null) {
  const modalTitle = document.getElementById("createOfferModalTitle");
  const form = document.getElementById("offerForm");

  if (offer) {
    modalTitle.textContent = "تعديل العرض";
    currentEditingOfferId = offer.id;
    document.getElementById("offerId").value = offer.id;
    document.getElementById("offerTitle").value = offer.title || "";
    document.getElementById("offerNumberOfSessions").value =
      offer.numberOfSessions || "";
    document.getElementById("offerNumberOfMonths").value =
      offer.numberOfMonths || "";
    document.getElementById("offerPrice").value = offer.price || "";
    document.getElementById("offerStatus").value = offer.status || "ACTIVE";
  } else {
    modalTitle.textContent = "إنشاء عرض جديد";
    currentEditingOfferId = null;
    form.reset();
    document.getElementById("offerStatus").value = "ACTIVE";
  }

  document.getElementById("createOfferModal").style.display = "block";
}

// Close create offer modal
function closeCreateOfferModal() {
  document.getElementById("createOfferModal").style.display = "none";
  currentEditingOfferId = null;
  document.getElementById("offerForm").reset();
}

// Edit an offer
async function editOffer(id) {
  try {
    const response = await fetch(`/api/offers/${id}`);
    if (!response.ok) {
      throw new Error(`Server returned ${response.status}`);
    }

    const offer = await response.json();
    openCreateOfferModal(offer);
  } catch (error) {
    console.error("Error fetching offer:", error);
    showNotification("error", "خطأ", "فشل في تحميل تفاصيل العرض");
  }
}

// Handle offer form submission
async function handleOfferFormSubmit(event) {
  event.preventDefault();

  const offerData = {
    title: document.getElementById("offerTitle").value,
    numberOfSessions: parseInt(
      document.getElementById("offerNumberOfSessions").value
    ),
    numberOfMonths: parseInt(
      document.getElementById("offerNumberOfMonths").value
    ),
    price: parseFloat(document.getElementById("offerPrice").value),
    status: document.getElementById("offerStatus").value,
  };

  try {
    let response;
    let url;

    if (currentEditingOfferId) {
      url = `/api/offers/${currentEditingOfferId}`;
      response = await fetch(url, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(offerData),
      });
    } else {
      url = "/api/offers";
      response = await fetch(url, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(offerData),
      });
    }

    if (response.ok) {
      closeCreateOfferModal();
      await loadOffers();

      showNotification(
        "success",
        "تم بنجاح",
        currentEditingOfferId
          ? "تم تحديث العرض بنجاح!"
          : "تم إنشاء العرض بنجاح!"
      );
    } else {
      const errorText = await response.text();
      throw new Error(errorText || `Server returned ${response.status}`);
    }
  } catch (error) {
    console.error("Error saving offer:", error);
    showNotification("error", "خطأ", "فشل في حفظ العرض: " + error.message);
  }
}

// Delete an offer
async function deleteOffer(id) {
  if (!confirm("هل أنت متأكد من أنك تريد حذف هذا العرض؟")) {
    return;
  }

  try {
    const response = await fetch(`/api/offers/${id}`, {
      method: "DELETE",
    });

    if (response.ok) {
      await loadOffers();
      showNotification("success", "تم بنجاح", "تم حذف العرض بنجاح!");
    } else {
      throw new Error(`Server returned ${response.status}`);
    }
  } catch (error) {
    console.error("Error deleting offer:", error);
    showNotification("error", "خطأ", "فشل في حذف العرض: " + error.message);
  }
}

// Setup modal click outside handlers
setupModalClickOutside("createOfferModal", closeCreateOfferModal);

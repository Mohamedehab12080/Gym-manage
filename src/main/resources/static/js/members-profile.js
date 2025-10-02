// Members profile-specific JavaScript functionality

// View member profile in modal
async function viewMemberProfile(id) {
  try {
    showProfileLoading(true);

    // Load member data
    const memberResponse = await fetch(`/api/members/${id}`);
    if (!memberResponse.ok) {
      throw new Error(`Failed to fetch member: ${memberResponse.status}`);
    }
    profileMemberData = await memberResponse.json();

    // Load workout sessions
    const sessionsResponse = await fetch(`/api/workout-sessions/member/${id}`);
    if (sessionsResponse.ok) {
      profileWorkoutSessions = await sessionsResponse.json();
      filteredSessions = [...profileWorkoutSessions];
    } else {
      console.error(
        "Failed to fetch workout sessions:",
        sessionsResponse.status
      );
      profileWorkoutSessions = [];
      filteredSessions = [];
    }

    // Load member offers
    const offersResponse = await fetch(
      `/api/member-offers/member/${id}/active`
    );
    if (offersResponse.ok) {
      profileMemberOffers = await offersResponse.json();
    } else {
      console.error("Failed to fetch member offers:", offersResponse.status);
      profileMemberOffers = [];
    }

    // Render the profile
    renderProfileModal();
    filterSessionsByDate();
    openProfileModal();
  } catch (error) {
    console.error("Error loading member profile:", error);
    showNotification("error", "خطأ", "فشل في تحميل الملف الشخصي للعضو");
  } finally {
    showProfileLoading(false);
  }
}

// Show/hide profile loading spinner
function showProfileLoading(show) {
  const spinner = document.getElementById("profileLoadingSpinner");
  const tableBody = document.getElementById("sessionsTableBody");

  if (spinner) {
    spinner.style.display = show ? "block" : "none";
  }

  if (show) {
    tableBody.innerHTML = `
            <tr>
                <td colspan="2" style="text-align: center;">
                    <div class="spinner"></div>
                    <div>جاري تحميل الجلسات...</div>
                </td>
            </tr>
        `;
  }
}

// Render profile modal
function renderProfileModal() {
  if (!profileMemberData) return;
  const addSessionButton = document.querySelector(".btn-success.btn-sm");
  addSessionButton.disabled = false;
  addSessionButton.title = "إضافة جلسة";
  addSessionButton.classList.remove("disabled");

  // Set profile image
  const profileImage = document.getElementById("profileImage");
  if (profileMemberData.imageUrl) {
    profileImage.innerHTML = `<img src="${profileMemberData.imageUrl}" alt="${profileMemberData.fullName}" style="width: 100%; height: 100%; border-radius: 50%; object-fit: cover;">`;
  } else {
    profileImage.innerHTML = '<i class="fas fa-user"></i>';
  }

  // Set profile details
  document.getElementById("profileName").textContent =
    profileMemberData.fullName || "غير متاح";
  document.getElementById("profileId").textContent =
    profileMemberData.id || "غير متاح";
  document.getElementById("profilePhone").textContent =
    profileMemberData.phone || "غير متاح";
  document.getElementById("profileJoinDate").textContent =
    profileMemberData.joinDate || "غير متاح";

  // Set status
  const statusElement = document.getElementById("profileStatus");
  if (profileMemberData.status) {
    statusElement.innerHTML =
      '<span class="status-badge status-active">نشط</span>';
  } else {
    statusElement.innerHTML =
      '<span class="status-badge status-inactive">غير نشط</span>';
  }

  // Set membership type
  const membershipElement = document.getElementById("profileMembership");
  const membershipType = profileMemberData.membershipType || "NORMAL";
  membershipElement.innerHTML = `<span class="membership-badge membership-${membershipType}">${
    membershipType === "NORMAL"
      ? "عادي"
      : membershipType === "PREMIUM"
      ? "مميز"
      : "VIP"
  }</span>`;

  // Render subscription info
  renderProfileSubscriptionInfo();

  // Render workout sessions
  renderProfileWorkoutSessions();
}

// Render subscription information in profile modal
function renderProfileSubscriptionInfo() {
  const activeOffer =
    profileMemberOffers.length > 0 ? profileMemberOffers[0] : null;

  if (!activeOffer) {
    document.getElementById("subscriptionOffer").textContent =
      "لا يوجد اشتراك نشط";
    document.getElementById("subscriptionStart").textContent = "-";
    document.getElementById("subscriptionEnd").textContent = "-";
    document.getElementById("subscriptionRemaining").textContent = "-";
    document.getElementById("subscriptionStatus").textContent = "لا يوجد";
    document.querySelector(".subscription-card").style.opacity = "0.7";
    return;
  }

  document.getElementById("subscriptionOffer").textContent = activeOffer.offer
    ? activeOffer.offer.title
    : `العرض #${activeOffer.offerId}`;
  document.getElementById("subscriptionStart").textContent =
    activeOffer.startDate || "-";
  document.getElementById("subscriptionEnd").textContent =
    activeOffer.endDate || "-";
  document.getElementById("subscriptionRemaining").textContent =
    activeOffer.remainedSessions !== undefined
      ? activeOffer.remainedSessions
      : "-";
  document.getElementById("subscriptionPrice").textContent = activeOffer.offer
    ? activeOffer.offer.price
    : "-";

  const statusElement = document.getElementById("subscriptionStatus");
  statusElement.textContent = activeOffer.status || "-";

  // Add color coding based on status
  if (activeOffer.status === "ACTIVE") {
    statusElement.style.backgroundColor = "rgba(76, 201, 240, 0.3)";
  } else if (activeOffer.status === "EXPIRED") {
    statusElement.style.backgroundColor = "rgba(229, 56, 59, 0.3)";
  } else if (activeOffer.status === "CANCELLED") {
    statusElement.style.backgroundColor = "rgba(108, 117, 125, 0.3)";
  }
}

// Render workout sessions in profile modal
function renderProfileWorkoutSessions() {
  const tableBody = document.getElementById("sessionsTableBody");
  const addSessionButton = document.querySelector(".btn-success.btn-sm");
  if (!filteredSessions || !Array.isArray(filteredSessions)) {
    tableBody.innerHTML =
      '<tr><td colspan="3" class="error-message">تنسيق بيانات الجلسات غير صالح</td></tr>';
    addSessionButton.disabled = false;
    addSessionButton.title = "إضافة جلسة";
    addSessionButton.classList.remove("disabled");
    return;
  }

  if (filteredSessions.length === 0) {
    tableBody.innerHTML = `
        <tr>
            <td colspan="3" style="text-align: center; padding: 30px;">
                <i class="fas fa-dumbbell" style="font-size: 3rem; opacity: 0.3; margin-bottom: 15px; display: block;"></i>
                <div>لم يتم العثور على جلسات تمرين</div>
            </td>
        </tr>`;
    addSessionButton.disabled = false;
    addSessionButton.title = "إضافة جلسة";
    addSessionButton.classList.remove("disabled");
    return;
  }

  tableBody.innerHTML = "";

  // Sort sessions by date (newest first)
  const sortedSessions = [...filteredSessions].sort(
    (a, b) => new Date(b.sessionDate) - new Date(a.sessionDate)
  );

  // Flag to track if current date exists in sessions
  let hasCurrentDateSession = false;

  sortedSessions.forEach((session, index) => {
    const row = document.createElement("tr");

    // Calculate remaining sessions for this date
    const remainingSessions = session?.remainedSessions;

    // Determine CSS class for remaining sessions
    let remainingClass = "remaining-high";
    if (remainingSessions <= 3) {
      remainingClass = "remaining-low";
    } else if (remainingSessions <= 7) {
      remainingClass = "remaining-medium";
    }

    // Format session date for comparison
    const sessionDateFormatted = formatDate(session.sessionDate);

    // Get current date in the same format
    const currentDate = new Date();
    const currentDateFormatted = formatDate(currentDate);

    // Check if session date equals current date
    if (sessionDateFormatted === currentDateFormatted) {
      hasCurrentDateSession = true;
    }

    row.innerHTML = `
    <td class="session-date">${sessionDateFormatted || "غير متاح"}</td>
    <td><span class="remaining-sessions ${remainingClass}">${remainingSessions}</span></td>
    <td class="session-actions-cell">
        <button class="btn-icon btn-delete" onclick="deleteWorkoutSession(${
          session.id
        })" title="حذف الجلسة">
            <i class="fas fa-trash"></i>
        </button>
    </td>
`;

    tableBody.appendChild(row);
  });

  // Disable or enable the Add Session button based on whether current date exists
  if (hasCurrentDateSession) {
    addSessionButton.disabled = true;
    addSessionButton.title = "لا يمكن إضافة أكثر من جلسة واحدة في اليوم";
    addSessionButton.classList.add("disabled");
  } else {
    addSessionButton.disabled = false;
    addSessionButton.title = "إضافة جلسة";
    addSessionButton.classList.remove("disabled");
  }
}

// Function to delete a workout session
function deleteWorkoutSession(sessionId) {
  if (confirm("هل أنت متأكد أنك تريد حذف جلسة التمرين هذه؟")) {
    // Show loading state
    showNotification("info", "معلومات", "جاري حذف الجلسة...");

    // Make API call to delete the session
    fetch(`/api/workout-sessions/${sessionId}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Failed to delete session");
        }
        return response.json();
      })
      .then((data) => {
        // Remove the session from the filteredSessions array
        filteredSessions = filteredSessions.filter(
          (session) => session.id !== sessionId
        );

        // Re-render the sessions table
        renderProfileWorkoutSessions();

        // Show success message
        showNotification("success", "نجاح", "تم حذف الجلسة بنجاح");
      })
      .catch((error) => {
        console.error("Error deleting workout session:", error);
        showNotification("error", "خطأ", "فشل في حذف الجلسة: " + error.message);
      });
  }
}

// Filter sessions by date range
function filterSessionsByDate() {
  const startDate = document.getElementById("startDateFilter").value;
  const endDate = document.getElementById("endDateFilter").value;

  if (!startDate || !endDate) {
    filteredSessions = [...profileWorkoutSessions];
  } else {
    const start = new Date(startDate);
    const end = new Date(endDate);
    end.setHours(23, 59, 59, 999); // Include the entire end date

    filteredSessions = profileWorkoutSessions.filter((session) => {
      const sessionDate = new Date(session.sessionDate);
      return sessionDate >= start && sessionDate <= end;
    });
  }

  renderProfileWorkoutSessions();
}

// Open profile modal
function openProfileModal() {
  document.getElementById("memberProfileModal").style.display = "block";
}

// Close profile modal
function closeProfileModal() {
  document.getElementById("memberProfileModal").style.display = "none";
  // Reset profile data
  profileMemberData = null;
  profileWorkoutSessions = [];
  profileMemberOffers = [];
  filteredSessions = [];
}

// Open create workout session modal
function openCreateWorkoutSessionModal() {
  if (!profileMemberData) return;

  document.getElementById("workoutSessionMemberId").value =
    profileMemberData.id;
  document.getElementById("sessionDate").valueAsDate = new Date();
  document.getElementById("createWorkoutSessionModal").style.display = "block";
}

// Close create workout session modal
function closeCreateWorkoutSessionModal() {
  document.getElementById("createWorkoutSessionModal").style.display = "none";
  document.getElementById("workoutSessionForm").reset();
}

// Handle workout session form submission
async function handleWorkoutSessionSubmit(event) {
  event.preventDefault();

  const memberId = document.getElementById("workoutSessionMemberId").value;
  const sessionDate = document.getElementById("sessionDate").value;

  try {
    const response = await fetch("/api/workout-sessions", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        memberId: parseInt(memberId),
        sessionDate: sessionDate,
      }),
    });

    if (response.ok) {
      closeCreateWorkoutSessionModal();
      showNotification("success", "نجاح", "تم إنشاء جلسة التمرين بنجاح!");

      // Reload the profile to see the updated sessions
      await viewMemberProfile(parseInt(memberId));
    } else {
      const errorText = await response.text();
      throw new Error(
        errorText ||
          `Server returned ${response.status}: ${response.statusText}`
      );
    }
  } catch (error) {
    console.error("Error creating workout session:", error);
    showNotification(
      "error",
      "خطأ",
      "فشل في إنشاء جلسة التمرين: " + error.message
    );
  }
}

// Open create member offer modal
async function openCreateMemberOfferModal() {
  if (!profileMemberData) return;

  try {
    // Load available offers
    const response = await fetch("/api/offers");
    if (response.ok) {
      allOffers = await response.json();

      // Check if offers array is empty
      if (!allOffers || allOffers.length === 0) {
        showNotification(
          "warning",
          "تحذير",
          "لا توجد عروض متاحة. يرجى إنشاء عروض أولاً."
        );
        return;
      }

      const offerSelect = document.getElementById("offerId");
      offerSelect.innerHTML = '<option value="">اختر عرضًا</option>';

      allOffers.forEach((offer) => {
        const option = document.createElement("option");
        option.value = offer.id;

        // Use safe property access with fallbacks
        const title = offer.title || offer.name || "No Title";
        const sessions =
          offer.numberOfSessions ||
          offer.sessions ||
          offer.numberOfSession ||
          0;
        const price = offer.price || 0;
        const months = offer.numberOfMonths || offer.months || 0;

        option.textContent = `${title} - ${sessions} جلسة - $${price.toFixed(
          2
        )}`;
        option.setAttribute("data-sessions", sessions);
        option.setAttribute("data-months", months);
        option.setAttribute("data-price", price);
        offerSelect.appendChild(option);
      });

      document.getElementById("memberOfferMemberId").value =
        profileMemberData.id;

      // Set default dates (today and based on offer months)
      const today = new Date();

      // Set today as the start date
      document.getElementById("memberOfferStartDate").valueAsDate = today;

      // Initially set end date to 30 days from now as fallback
      const defaultEndDate = new Date();
      defaultEndDate.setDate(today.getDate() + 30);
      document.getElementById("memberOfferEndDate").valueAsDate =
        defaultEndDate;

      // Add event listener to update dates when offer selection changes
      offerSelect.addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];

        if (selectedOption.value) {
          const months = parseInt(selectedOption.getAttribute("data-months"));
          const startDate = new Date();

          // Calculate end date based on months in offer
          const endDate = new Date();
          endDate.setMonth(startDate.getMonth() + months);

          // Update the date fields
          document.getElementById("memberOfferStartDate").valueAsDate =
            startDate;
          document.getElementById("memberOfferEndDate").valueAsDate = endDate;
        } else {
          // Reset to defaults if no offer selected
          const today = new Date();
          const in30Days = new Date();
          in30Days.setDate(today.getDate() + 30);
          document.getElementById("memberOfferStartDate").valueAsDate = today;
          document.getElementById("memberOfferEndDate").valueAsDate = in30Days;
        }
      });

      document.getElementById("createMemberOfferModal").style.display = "block";
    } else {
      throw new Error(`Failed to fetch offers: ${response.status}`);
    }
  } catch (error) {
    console.error("Error loading offers:", error);
    showNotification("error", "خطأ", "فشل في تحميل العروض: " + error.message);
  }
}

// Close create member offer modal
function closeCreateMemberOfferModal() {
  document.getElementById("createMemberOfferModal").style.display = "none";
  document.getElementById("memberOfferForm").reset();
}

// Handle member offer form submission
async function handleMemberOfferSubmit(event) {
  event.preventDefault();

  const memberId = document.getElementById("memberOfferMemberId").value;
  const offerId = document.getElementById("offerId").value;
  const startDate = document.getElementById("memberOfferStartDate").value;
  const endDate = document.getElementById("memberOfferEndDate").value;
  const remainedSessions = document.getElementById("remainedSessions").value;

  try {
    const response = await fetch("/api/member-offers", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        memberId: parseInt(memberId),
        offerId: parseInt(offerId),
        startDate: startDate,
        endDate: endDate,
        remainedSessions: parseInt(remainedSessions),
        status: "ACTIVE",
      }),
    });

    if (response.ok) {
      closeCreateMemberOfferModal();
      showNotification("success", "نجاح", "تم إنشاء عرض العضو بنجاح!");

      // Reload the profile to see the updated offer
      await viewMemberProfile(parseInt(memberId));
    } else {
      const errorText = await response.text();
      throw new Error(
        errorText ||
          `Server returned ${response.status}: ${response.statusText}`
      );
    }
  } catch (error) {
    console.error("Error creating member offer:", error);
    showNotification(
      "error",
      "خطأ",
      "فشل في إنشاء عرض العضو: " + error.message
    );
  }
}

// Setup modal click outside handlers
setupModalClickOutside("memberProfileModal", closeProfileModal);
setupModalClickOutside(
  "createWorkoutSessionModal",
  closeCreateWorkoutSessionModal
);
setupModalClickOutside("createMemberOfferModal", closeCreateMemberOfferModal);

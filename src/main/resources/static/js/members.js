// Members-specific JavaScript functionality

// Global variables
let allMembers = [];
let currentEditingId = null;
let profileMemberData = null;
let profileWorkoutSessions = [];
let profileMemberOffers = [];
let allOffers = [];
let filteredSessions = [];

// DOM Content Loaded
document.addEventListener("DOMContentLoaded", function () {
  loadMembers();
  // Set default dates for filters to current month
  const today = new Date();
  const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
  const lastDayOfMonth = new Date(today.getFullYear(), today.getMonth() + 1, 0);

  document.getElementById("startDateFilter").valueAsDate = firstDayOfMonth;
  document.getElementById("endDateFilter").valueAsDate = lastDayOfMonth;
});

// Load all members from the server
async function loadMembers() {
  try {
    showLoading(true);
    // Call the members endpoint that returns JSON
    const response = await fetch("/api/members");

    if (!response.ok) {
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }

    // Parse JSON response
    const members = await response.json();
    console.log("Loaded members:", members);
    console.log("Members count:", members.length);
    console.log("Members type:", typeof members);
    console.log("Is array:", Array.isArray(members));

    allMembers = members;
    renderMembersTable(allMembers);
    updateStats();

    showNotification("success", "نجاح", "تم تحميل الأعضاء بنجاح");
  } catch (error) {
    console.error("Error loading members:", error);
    document.getElementById("membersTableBody").innerHTML =
      '<tr><td colspan="8" class="error-message">حدث خطأ في تحميل الأعضاء. يرجى التحقق من وحدة التحكم للتفاصيل.</td></tr>';

    showNotification(
      "error",
      "خطأ",
      "فشل في تحميل الأعضاء. يرجى المحاولة مرة أخرى."
    );
  } finally {
    showLoading(false);
  }
}

// Render members table
function renderMembersTable(members) {
  console.log("renderMembersTable called with:", members);
  const tableBody = document.getElementById("membersTableBody");
  console.log("Table body element:", tableBody);

  if (!members || !Array.isArray(members)) {
    console.log("Invalid members data:", members);
    tableBody.innerHTML =
      '<tr><td colspan="8" class="error-message">تنسيق بيانات الأعضاء غير صالح</td></tr>';
    return;
  }

  if (members.length === 0) {
    console.log("No members found");
    tableBody.innerHTML =
      '<tr><td colspan="8" style="text-align: center;">لم يتم العثور على أعضاء</td></tr>';
    return;
  }

  console.log("Rendering", members.length, "members");

  tableBody.innerHTML = "";

  members.forEach((member) => {
    const row = document.createElement("tr");

    // Use imageUrl for display instead of imageData
    const imageSrc = member.imageUrl || "";

    row.innerHTML = `
            <td>${member.id || "غير متاح"}</td>
            <td>
                <div class="member-avatar">
                    ${
                      imageSrc
                        ? `<img src="${imageSrc}" alt="${member.fullName}" class="member-avatar" style="width: 45px; height: 45px; border-radius: 50%; object-fit: cover;">`
                        : `<i class="fas fa-user"></i>`
                    }
                </div>
            </td>
            <td>${member.fullName || "غير متاح"}</td>
            <td>${member.joinDate || "غير متاح"}</td>
            <td>${member.phone || "غير متاح"}</td>
            <td><span class="status-badge ${
              member.status ? "status-active" : "status-inactive"
            }">${member.status ? "نشط" : "غير نشط"}</span></td>
            <td><span class="membership-badge membership-${
              member.membershipType || "NORMAL"
            }">${member.membershipType || "عادي"}</span></td>
            <td class="actions-cell">
                <button class="btn btn-edit" onclick="editMember(${member.id})">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-danger" onclick="deleteMember(${
                  member.id
                })">
                    <i class="fas fa-trash"></i>
                </button>
                <button class="btn btn-info" onclick="viewMemberProfile(${
                  member.id
                })">
                    <i class="fas fa-user"></i> الملف
                </button>
            </td>
        `;

    tableBody.appendChild(row);
  });
}

// Update statistics
function updateStats() {
  console.log("updateStats called, allMembers:", allMembers);
  // Total members
  const totalCount = allMembers.length;
  console.log("Total count:", totalCount);
  document.getElementById("totalMembers").textContent = totalCount;

  // Active members count
  const activeCount = allMembers.filter((member) => member.status).length;
  console.log("Active count:", activeCount);
  document.getElementById("activeMembers").textContent = activeCount;
}

// Filter members based on status
function filterMembers() {
  const filterValue = document.getElementById("statusFilter").value;

  let filteredMembers = allMembers;
  if (filterValue === "active") {
    filteredMembers = allMembers.filter((member) => member.status);
  } else if (filterValue === "inactive") {
    filteredMembers = allMembers.filter((member) => !member.status);
  }

  renderMembersTable(filteredMembers);
}

// Search members
function searchMembers() {
  const searchValue = document
    .getElementById("searchInput")
    .value.toLowerCase();
  const searchNumber = document
    .getElementById("searchNumber")
    .value.toLowerCase();

  const filteredMembers = allMembers.filter((member) => {
    const nameMatch = searchValue
      ? member.fullName && member.fullName.toLowerCase().includes(searchValue)
      : true;
    const phoneMatch = searchNumber
      ? member.phone && member.phone.toLowerCase().includes(searchNumber)
      : true;

    return nameMatch && phoneMatch;
  });

  renderMembersTable(filteredMembers);
}

// Preview selected image
function previewImage(input) {
  const preview = document.getElementById("imagePreview");
  if (input.files && input.files[0]) {
    const reader = new FileReader();
    reader.onload = function (e) {
      preview.src = e.target.result;
      preview.style.display = "block";
    };
    reader.readAsDataURL(input.files[0]);
  } else {
    preview.style.display = "none";
  }
}

function openMemberModal(member = null) {
  const modal = document.getElementById("memberModal");
  const modalTitle = document.getElementById("modalTitle");
  const form = document.getElementById("memberForm");
  const preview = document.getElementById("imagePreview");

  if (member) {
    // Editing existing member
    modalTitle.textContent = "تعديل العضو";
    currentEditingId = member.id;
    document.getElementById("memberId").value = member.id;
    document.getElementById("fullName").value = member.fullName || "";
    document.getElementById("phone").value = member.phone || "";
    document.getElementById("membershipType").value =
      member.membershipType || "NORMAL";
    document.getElementById("status").checked = member.status || false;

    // Show existing image using imageUrl
    if (member.imageUrl) {
      preview.src = member.imageUrl;
      preview.style.display = "block";
    } else {
      preview.style.display = "none";
    }
  } else {
    // Creating new member
    modalTitle.textContent = "إضافة عضو جديد";
    currentEditingId = null;
    form.reset();
    preview.style.display = "none";
  }

  modal.style.display = "block";
}

// Edit a member
async function editMember(id) {
  try {
    const response = await fetch(`/api/members/${id}`);
    if (!response.ok) {
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }

    const member = await response.json();
    openMemberModal(member);

    showNotification("info", "معلومات", "جاري تحميل تفاصيل العضو...");
  } catch (error) {
    console.error("Error fetching member:", error);
    showNotification(
      "error",
      "خطأ",
      "فشل في تحميل تفاصيل العضو: " + error.message
    );
  }
}

// Close member modal
function closeMemberModal() {
  document.getElementById("memberModal").style.display = "none";
  currentEditingId = null;
  document.getElementById("memberForm").reset();
  document.getElementById("imagePreview").style.display = "none";
}

// Handle form submission
async function handleFormSubmit(event) {
  event.preventDefault();

  const formData = new FormData();

  // Add all form fields to FormData
  formData.append("fullName", document.getElementById("fullName").value);
  formData.append("phone", document.getElementById("phone").value);
  formData.append(
    "membershipType",
    document.getElementById("membershipType").value
  );
  formData.append("status", document.getElementById("status").checked);

  // Add image file if selected
  const imageFile = document.getElementById("imageFile").files[0];
  if (imageFile) {
    formData.append("imageFile", imageFile);
  }

  try {
    let response;
    let url;

    if (currentEditingId) {
      // Update existing member
      url = `/api/members/${currentEditingId}`;
      response = await fetch(url, {
        method: "PUT",
        body: formData,
      });
    } else {
      // Create new member
      url = "/api/members";
      response = await fetch(url, {
        method: "POST",
        body: formData,
      });
    }

    if (response.ok) {
      closeMemberModal();

      // Reload the page to see the updated members list
      window.location.reload();

      showNotification(
        "success",
        "نجاح",
        currentEditingId ? "تم تحديث العضو بنجاح!" : "تم إنشاء العضو بنجاح!"
      );
    } else {
      // Check if it's a duplicate phone number error
      if (response.status === 409) {
        const errorData = await response.json();
        showNotification(
          "error",
          "خطأ",
          `رقم الهاتف مستخدم بالفعل: ${
            errorData.message || "رقم الهاتف مستخدم من قبل عضو آخر"
          }`
        );
      } else {
        const errorText = await response.text();
        throw new Error(
          errorText ||
            `Server returned ${response.status}: ${response.statusText}`
        );
      }
    }
  } catch (error) {
    console.error("Error saving member:", error);

    // Don't show the generic error for duplicate phone numbers as we already handled it
    if (!error.message.includes("رقم الهاتف مستخدم")) {
      showNotification("error", "خطأ", "فشل في حفظ العضو: " + error.message);
    }
  }
}

// Delete a member
async function deleteMember(id) {
  if (!confirm("هل أنت متأكد أنك تريد حذف هذا العضو؟")) {
    return;
  }

  try {
    showNotification("info", "معلومات", "جاري حذف العضو...");

    const response = await fetch(`/api/members/${id}`, {
      method: "DELETE",
    });

    if (response.ok) {
      // Reload the page to see the updated members list
      window.location.reload();
      showNotification("success", "نجاح", "تم حذف العضو بنجاح!");
    } else {
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }
  } catch (error) {
    console.error("Error deleting member:", error);
    showNotification("error", "خطأ", "فشل في حذف العضو: " + error.message);
  }
}

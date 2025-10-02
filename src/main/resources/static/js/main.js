// Common JavaScript functionality for BOB Gym Management System

// Define navigation and footer templates
const navigationTemplate = `
    <header>
        <div class="container">
            <nav>
                <a href="/" class="logo">
                    <i class="fas fa-dumbbell"></i>
                    <span>نادي BOB</span>
                </a>

                <button class="menu-toggle" id="menuToggle">
                    <span></span>
                    <span></span>
                    <span></span>
                </button>

                <div class="nav-links" id="navLinks">
                    <a href="/">الرئيسية</a>
                    <a href="/members">الأعضاء</a>
                    <a href="/offers">العروض</a>
                    <a href="#">الفصول</a>
                    <a href="#">المدربون</a>
                    <a href="#">اتصل بنا</a>
                </div>
            </nav>
        </div>
    </header>
`;

const footerTemplate = `
    <footer>
        <div class="container">
            <div class="footer-content">
                <div class="footer-column">
                    <h3>نظام إدارة نادي BOB</h3>
                    <p>نقدم حلول إدارة صالات رياضية متطورة لمساعدة أعمال اللياقة البدنية على الازدهار في العصر الرقمي.</p>
                    <div class="social-links">
                        <a href="#"><i class="fab fa-facebook-f"></i></a>
                        <a href="#"><i class="fab fa-twitter"></i></a>
                        <a href="#"><i class="fab fa-instagram"></i></a>
                        <a href="#"><i class="fab fa-linkedin-in"></i></a>
                    </div>
                </div>

                <div class="footer-column">
                    <h3>روابط سريعة</h3>
                    <ul class="footer-links">
                        <li><a href="/">الرئيسية</a></li>
                        <li><a href="/members">الأعضاء</a></li>
                        <li><a href="/offers">العروض</a></li>
                        <li><a href="#">المدربون</a></li>
                        <li><a href="#">الأسعار</a></li>
                    </ul>
                </div>

                <div class="footer-column">
                    <h3>المصادر</h3>
                    <ul class="footer-links">
                        <li><a href="#">المدونة</a></li>
                        <li><a href="#">التوثيق</a></li>
                        <li><a href="#">مركز المساعدة</a></li>
                        <li><a href="#">توثيق API</a></li>
                    </ul>
                </div>

                <div class="footer-column">
                    <h3>اتصل بنا</h3>
                    <p><i class="fas fa-map-marker-alt"></i> شارع اللياقة 123، مدينة الصحة</p>
                    <p><i class="fas fa-phone"></i> 555 123 4567+</p>
                    <p><i class="fas fa-envelope"></i> info@bobgym.com</p>
                </div>
            </div>

            <div class="copyright">
                <p>© 2024 نظام إدارة نادي BOB. جميع الحقوق محفوظة.</p>
            </div>
        </div>
    </footer>
`;

// Function to load navigation
function loadNavigation() {
  // Try to fetch from server first
  fetch("/components/navigation.html")
    .then((response) => {
      if (!response.ok) {
        throw new Error("Navigation file not found");
      }
      return response.text();
    })
    .then((data) => {
      document.getElementById("navbar").innerHTML = data;
      initNavigation();
    })
    .catch((error) => {
      console.log("Using embedded navigation template");
      // Use the embedded template if fetch fails
      document.getElementById("navbar").innerHTML = navigationTemplate;
      initNavigation();
    });
}

// Function to load footer
function loadFooter() {
  // Try to fetch from server first
  fetch("/components/footer.html")
    .then((response) => {
      if (!response.ok) {
        throw new Error("Footer file not found");
      }
      return response.text();
    })
    .then((data) => {
      document.getElementById("footer").innerHTML = data;
    })
    .catch((error) => {
      console.log("Using embedded footer template");
      // Use the embedded template if fetch fails
      document.getElementById("footer").innerHTML = footerTemplate;
    });
}

// Initialize navigation functionality
function initNavigation() {
  // Mobile menu toggle
  const menuToggle = document.getElementById("menuToggle");
  const navLinks = document.getElementById("navLinks");

  if (menuToggle && navLinks) {
    menuToggle.addEventListener("click", () => {
      navLinks.classList.toggle("active");
      menuToggle.classList.toggle("active");
    });
  }

  // Highlight current page in navigation
  const currentLocation = window.location.pathname;
  const navItems = document.querySelectorAll(".nav-links a");

  navItems.forEach((item) => {
    const itemPath = item.getAttribute("href");

    // Handle root path
    if (currentLocation === "/" && itemPath === "/") {
      item.classList.add("active");
      return;
    }

    // Handle other paths
    if (
      currentLocation !== "/" &&
      itemPath !== "/" &&
      currentLocation.includes(itemPath)
    ) {
      item.classList.add("active");
    } else if (currentLocation !== itemPath) {
      item.classList.remove("active");
    }
  });

  // Close mobile menu when clicking outside
  document.addEventListener("click", (e) => {
    if (
      navLinks &&
      navLinks.classList.contains("active") &&
      !e.target.closest(".nav-links") &&
      !e.target.closest(".menu-toggle")
    ) {
      navLinks.classList.remove("active");
      menuToggle.classList.remove("active");
    }
  });
}

// Common notification system
function showNotification(type, title, message, duration = 3000) {
  const notificationContainer = document.getElementById(
    "notificationContainer"
  );
  if (!notificationContainer) {
    console.warn("Notification container not found");
    return;
  }

  const notification = document.createElement("div");
  notification.className = `notification notification-${type}`;

  let iconClass = "fa-info-circle";
  switch (type) {
    case "success":
      iconClass = "fa-check-circle";
      break;
    case "error":
      iconClass = "fa-exclamation-circle";
      break;
    case "warning":
      iconClass = "fa-exclamation-triangle";
      break;
    case "info":
      iconClass = "fa-info-circle";
      break;
  }

  notification.innerHTML = `
        <i class="fas ${iconClass} notification-icon"></i>
        <div class="notification-content">
            <div class="notification-title">${title}</div>
            <div class="notification-message">${message}</div>
        </div>
        <button class="notification-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;

  notificationContainer.appendChild(notification);

  // Auto remove after duration
  setTimeout(() => {
    if (notification.parentNode) {
      notification.remove();
    }
  }, duration);
}

// Common loading spinner functions
function showLoading(show, spinnerId = "loadingSpinner") {
  const spinner = document.getElementById(spinnerId);
  if (spinner) {
    spinner.style.display = show ? "block" : "none";
  }
}

// Common modal functions
function closeModal(modalId) {
  const modal = document.getElementById(modalId);
  if (modal) {
    modal.style.display = "none";
  }
}

// Close modal if clicked outside
function setupModalClickOutside(modalId, closeFunction) {
  window.addEventListener("click", function (event) {
    const modal = document.getElementById(modalId);
    if (event.target === modal) {
      closeFunction();
    }
  });
}

// Common form validation
function validateForm(formId) {
  const form = document.getElementById(formId);
  if (!form) return false;

  const requiredFields = form.querySelectorAll("[required]");
  let isValid = true;

  requiredFields.forEach((field) => {
    if (!field.value.trim()) {
      field.style.borderColor = "var(--danger)";
      isValid = false;
    } else {
      field.style.borderColor = "var(--gray-300)";
    }
  });

  return isValid;
}

// Common API call function
async function apiCall(url, options = {}) {
  try {
    const response = await fetch(url, {
      headers: {
        "Content-Type": "application/json",
        ...options.headers,
      },
      ...options,
    });

    if (!response.ok) {
      throw new Error(
        `Server returned ${response.status}: ${response.statusText}`
      );
    }

    return await response.json();
  } catch (error) {
    console.error("API call failed:", error);
    throw error;
  }
}

// Common date formatting
function formatDate(dateString) {
  if (!dateString) return "غير متاح";

  try {
    const date = new Date(dateString);
    return date.toLocaleDateString("en-CA"); // 'en-CA' locale gives yyyy-MM-dd format
  } catch (e) {
    return dateString;
  }
}

// Common currency formatting
function formatCurrency(amount) {
  return `$${amount ? parseFloat(amount).toFixed(2) : "0.00"}`;
}

// Load components when page is ready
document.addEventListener("DOMContentLoaded", function () {
  loadNavigation();
  loadFooter();
  console.log("تم تحميل نظام إدارة نادي BOB بنجاح");
});

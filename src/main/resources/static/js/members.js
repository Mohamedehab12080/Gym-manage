// Global variables
let allMembers = [];
let currentEditingId = null;

// DOM Content Loaded
document.addEventListener('DOMContentLoaded', function() {
    loadMembers();
    updateStats();
});

async function loadMembers() {
    try {
        const response = await fetch('/members/active');
        if (response.ok) {
            const data = await response.json();

            // Handle different response formats
            if (Array.isArray(data)) {
                allMembers = data;
            } else if (data.members && Array.isArray(data.members)) {
                allMembers = data.members;
            } else if (data.data && Array.isArray(data.data)) {
                allMembers = data.data;
            } else {
                console.error('Unexpected response format:', data);
                allMembers = [];
            }

            renderMembersTable(allMembers);
            await updateStats();
        } else {
            throw new Error('Failed to fetch members');
        }
    } catch (error) {
        console.error('Error loading members:', error);
        alert('Error loading members. Please try again.');
    }
}

// Render members table
function renderMembersTable(members) {
    const tableBody = document.getElementById('membersTableBody');
    tableBody.innerHTML = '';

    if (members.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="9" style="text-align: center;">No members found</td></tr>';
        return;
    }

    members.forEach(member => {
        const row = document.createElement('tr');

        // Format join date
        const joinDate = new Date(member.joinDate);
        const formattedDate = joinDate.toLocaleDateString();

        row.innerHTML = `
            <td>${member.id}</td>
            <td>
                <div class="member-avatar">
                    ${member.imageUrl
            ? `<img src="${member.imageUrl}" alt="${member.fullName}" class="member-avatar">`
            : `<i class="fas fa-user"></i>`
        }
                </div>
            </td>
            <td>${member.fullName}</td>
            <td>${member.email}</td>
            <td>${member.phone || 'N/A'}</td>
            <td>${formattedDate}</td>
            <td><span class="membership-badge membership-${member.membershipType}">${member.membershipType}</span></td>
            <td><span class="status-badge ${member.status ? 'status-active' : 'status-inactive'}">${member.status ? 'Active' : 'Inactive'}</span></td>
            <td class="actions-cell">
                <button class="btn btn-danger" onclick="deleteMember(${member.id})">
                    <i class="fas fa-trash"></i>
                </button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

// Update statistics
async function updateStats() {
    try {
        // Total members
        document.getElementById('totalMembers').textContent = allMembers.length;

        // Active members count
        const response = await fetch('/members/count-active');
        if (response.ok) {
            const count = await response.json();
            document.getElementById('activeMembers').textContent = count;
        }
    } catch (error) {
        console.error('Error updating stats:', error);
    }
}

// Filter members based on status
function filterMembers() {
    const filterValue = document.getElementById('statusFilter').value;

    let filteredMembers = allMembers;
    if (filterValue === 'active') {
        filteredMembers = allMembers.filter(member => member.status);
    } else if (filterValue === 'inactive') {
        filteredMembers = allMembers.filter(member => !member.status);
    }

    renderMembersTable(filteredMembers);
}

// Search members
function searchMembers() {
    const searchValue = document.getElementById('searchInput').value.toLowerCase();

    if (!searchValue) {
        renderMembersTable(allMembers);
        return;
    }

    const filteredMembers = allMembers.filter(member =>
        member.fullName.toLowerCase().includes(searchValue) ||
        member.email.toLowerCase().includes(searchValue) ||
        (member.phone && member.phone.toLowerCase().includes(searchValue))
    );

    renderMembersTable(filteredMembers);
}

// Open member modal for creating/editing
function openMemberModal(member = null) {
    const modal = document.getElementById('memberModal');
    const modalTitle = document.getElementById('modalTitle');
    const form = document.getElementById('memberForm');

    if (member) {
        // Editing existing member
        modalTitle.textContent = 'Edit Member';
        currentEditingId = member.id;
        document.getElementById('fullName').value = member.fullName;
        document.getElementById('email').value = member.email;
        document.getElementById('phone').value = member.phone || '';
        document.getElementById('membershipType').value = member.membershipType;
        document.getElementById('status').checked = member.status;
        document.getElementById('imageUrl').value = member.imageUrl || '';
    } else {
        // Creating new member
        modalTitle.textContent = 'Add New Member';
        currentEditingId = null;
        form.reset();
    }

    modal.style.display = 'block';
}

// Close member modal
function closeMemberModal() {
    document.getElementById('memberModal').style.display = 'none';
    currentEditingId = null;
}

// Handle form submission
async function handleFormSubmit(event) {
    event.preventDefault();

    const formData = new FormData(event.target);
    const memberData = {
        fullName: formData.get('fullName'),
        email: formData.get('email'),
        phone: formData.get('phone'),
        membershipType: formData.get('membershipType'),
        status: formData.get('status') === 'on',
        imageUrl: formData.get('imageUrl')
    };

    try {
        let response;

        if (currentEditingId) {
            // Update existing member
            response = await fetch(`/members/${currentEditingId}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(memberData)
            });
        } else {
            // Create new member
            response = await fetch('/members/create', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(memberData)
            });
        }

        if (response.ok) {
            closeMemberModal();
            loadMembers(); // Reload the members list
            alert(currentEditingId ? 'Member updated successfully!' : 'Member created successfully!');
        } else {
            throw new Error('Failed to save member');
        }
    } catch (error) {
        console.error('Error saving member:', error);
        alert('Error saving member. Please try again.');
    }
}

// Delete a member
async function deleteMember(id) {
    if (!confirm('Are you sure you want to delete this member?')) {
        return;
    }

    try {
        const response = await fetch(`/members/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadMembers(); // Reload the members list
            alert('Member deleted successfully!');
        } else {
            throw new Error('Failed to delete member');
        }
    } catch (error) {
        console.error('Error deleting member:', error);
        alert('Error deleting member. Please try again.');
    }
}

// Close modal if clicked outside
window.onclick = function(event) {
    const modal = document.getElementById('memberModal');
    if (event.target === modal) {
        closeMemberModal();
    }
};
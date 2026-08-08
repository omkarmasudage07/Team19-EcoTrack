# EcoTrack Frontend (React + Vite)

React 18, Vite, Bootstrap 5, React Router, Axios, Context API, React Hook Form.
Talks to the EcoTrack backend through the API Gateway.

## Run it

```bash
npm install
npm run dev
```

Opens at `http://localhost:5173`. Make sure the backend is running first
(Eureka → Config Server → Gateway → Auth/User/Pickup/Material/Notification
services - see the backend README for the exact order), since this app
calls the Gateway at `http://localhost:8080/api/v1` by default.

To point at a different Gateway URL, create a `.env.local` file:

```
VITE_API_URL=http://localhost:8080/api/v1
```

## What's built and working right now

- **Public**: landing page, "Become a Recycler Partner" and "Become an
  Industry Buyer" application forms (both submit to the real backend)
- **Auth**: Login, Register (Citizen), Forgot Password, Reset Password -
  all wired to the real Auth Service, with JWT stored and auto-refreshed
  on 401s
- **Citizen**: Dashboard (live EcoPoints balance, pending/completed
  pickup counts, recent activity), Schedule Pickup (real form, loads live
  waste categories), Pickup History (filterable, live status, cancel
  action), Notifications, Profile (view/edit)
- **Recycler**: Dashboard (live counts), Available Pickups (browse/accept/
  decline), My Pickups (advance status one step at a time), Material
  Listings (full CRUD - create/edit/delete with live categories),
  Orders (view received orders, advance fulfillment status), Notifications,
  Profile (read-only - see note below)
- **Industry**: Dashboard (live stats + recent orders), Marketplace
  (search/filter, buy-now flow with quantity + live stock validation),
  My Orders (mock payment, cancel before shipment), Notifications,
  Profile (read-only - see note below)
- **Admin**: Dashboard (platform-wide live stats), Users (Recycler/
  Industry accounts, suspend/reactivate), Recycler & Industry Applications
  (the full approve/reject workflow - approving creates login credentials
  automatically), Pickups/Materials/Orders oversight (read-only tables),
  Reports (status-breakdown charts via Chart.js), Audit Logs (real data),
  Settings (waste & material category CRUD)
- Shared plumbing every page reuses: role-aware sidebar/navbar, toast
  notifications, status badges, loading/empty/error states, role-based
  route protection, a reusable Notifications page used by every role

## Project status: feature-complete

Every route in every role now renders a real page backed by the live
API. The only exception is the Citizen EcoPoints wallet **detail**
screen (balance is already shown on the Citizen dashboard; the dedicated
transaction-history page is still a placeholder) - everything else is
wired end to end: register → schedule pickup → recycler accepts →
completes → citizen earns EcoPoints; recycler lists material → industry
buys → pays → recycler ships → delivered; recycler/industry apply →
admin approves → credentials created → they log in.

**Note on Recycler/Industry profile editing:** the backend's User Service
doesn't yet expose a self-service "update my profile" endpoint for
Recycler or Industry accounts (only Citizen has one) - so those Profile
pages are read-only by design, matching what the API actually supports.
Add `PUT /api/v1/recyclers/me` and `PUT /api/v1/industries/me` endpoints
to User Service first if you want these editable.

## Project structure

```
src/
  components/
    common/     - StatusBadge, PageHeader, Feedback states, ComingSoon
    layout/     - Sidebar, Navbar, DashboardLayout, per-role nav config
    cards/      - StatCard
  context/      - AuthContext, NotificationContext, ToastContext
  pages/
    public/     - Home, BecomeRecycler, BecomeIndustry
    auth/       - Login, Register, ForgotPassword, ResetPassword
    citizen/    - Dashboard, SchedulePickup, PickupHistory, Profile
    recycler/   - Dashboard, AvailablePickups, AssignedPickups,
                  MaterialListings, RecyclerOrders, Profile
    industry/   - Dashboard, Marketplace, IndustryOrders, Profile
    admin/      - Dashboard, AdminUsers, ApplicationReviewPage (shared
                  by Recycler/Industry applications), AdminPickups,
                  AdminMaterials, AdminOrders, Reports, AuditLogs,
                  AdminSettings
    shared/     - NotificationsPage (used by every role)
  routes/       - ProtectedRoute
  services/     - one file per backend microservice (authService,
                  userService, pickupService, materialService,
                  notificationService), all going through services/api.js
  styles/       - theme.css (design tokens - emerald green palette)
```

<template>
  <div class="shell" :class="{ 'auth-shell': !currentUser }">
    <aside v-if="currentUser" class="sidebar">
      <div class="brand">
        <div class="brand-mark">SC</div>
        <div>
          <strong>Complaint System</strong>
          <span>Operations Console</span>
        </div>
      </div>

      <nav v-if="currentUser" class="nav" aria-label="Primary navigation">
        <section v-for="group in navGroups" :key="group.label" class="nav-section">
          <span class="nav-label">{{ group.label }}</span>
          <button
            v-for="item in group.items"
            :key="item.view"
            :class="{ active: view === item.view }"
            @click="selectView(item.view)"
          >
            <component :is="item.icon" :size="18" aria-hidden="true" />
            <span>{{ item.label }}</span>
          </button>
        </section>
      </nav>

      <div v-if="currentUser" class="user-strip">
        <strong>{{ currentUser.fullName }}</strong>
        <span>{{ currentUser.role.replace('_', ' ') }}</span>
      </div>

      <button v-if="currentUser" class="logout" @click="logout">
        <LogOut :size="18" aria-hidden="true" />
        <span>Sign out</span>
      </button>
    </aside>

    <main class="main">
      <section
        v-if="!currentUser"
        class="cc-auth-page"
        :class="authMotionClasses"
        :style="authPointerStyle"
        @mousemove="handleAuthPointerMove"
        @mouseleave="resetAuthPointer"
      >
        <aside class="cc-showcase" aria-label="Complaint System brand illustration">
          <a class="cc-brand" href="#" @click.prevent>
            <div class="brand-mark">SC</div>
            <span>{{ brandName }}</span>
          </a>

          <div class="cc-characters" aria-hidden="true">
            <div ref="purpleBlock" class="cc-block cc-block-purple">
              <div class="cc-eyes cc-eyes-lg">
                <span><i></i></span>
                <span><i></i></span>
              </div>
            </div>
            <div ref="charcoalBlock" class="cc-block cc-block-charcoal">
              <div class="cc-eyes cc-eyes-md">
                <span><i></i></span>
                <span><i></i></span>
              </div>
            </div>
            <div ref="coralBlock" class="cc-block cc-block-coral">
              <div class="cc-dot-eyes">
                <span></span>
                <span></span>
              </div>
            </div>
            <div ref="yellowBlock" class="cc-block cc-block-yellow">
              <div class="cc-dot-eyes">
                <span></span>
                <span></span>
              </div>
              <i class="cc-mouth"></i>
            </div>
          </div>

        </aside>

        <section class="cc-login-side">
          <div class="cc-mobile-brand">
            <div class="brand-mark">SC</div>
            <span>{{ brandName }}</span>
          </div>

          <div class="cc-login-card">
            <header class="cc-login-header">
              <h1>{{ currentAuthConfig.title }}</h1>
              <p>{{ currentAuthConfig.subtitle }}</p>
            </header>

            <form v-if="authMode === 'login'" class="cc-login-form" @submit.prevent="handleLogin">
              <label class="cc-field" for="login-username">
                <span>Username</span>
                <input
                  id="login-username"
                  v-model="loginForm.username"
                  name="username"
                  autocomplete="username"
                  placeholder="student1"
                  @focus="setAuthTyping(true)"
                  @blur="setAuthTyping(false)"
                />
              </label>

              <label class="cc-field" for="password">
                <span>Password</span>
                <div class="cc-password-field">
                  <input
                    id="password"
                    v-model="loginForm.password"
                    name="password"
                    :type="showPassword ? 'text' : 'password'"
                    autocomplete="current-password"
                    placeholder="••••••••"
                  />
                  <button type="button" class="cc-icon-button" :aria-label="showPassword ? 'Hide password' : 'Show password'" @click="showPassword = !showPassword">
                    <component :is="showPassword ? EyeOff : Eye" :size="20" aria-hidden="true" />
                  </button>
                </div>
              </label>

              <div class="cc-form-row">
                <label class="cc-remember" for="remember">
                  <input id="remember" v-model="rememberLogin" type="checkbox" />
                  <span>Remember for 30 days</span>
                </label>
                <a href="#" @click.prevent="setAuthMode('forgot')">Forgot password?</a>
              </div>

              <button class="cc-motion-button" type="submit" :disabled="loading">
                <span>{{ loading ? 'Logging in...' : 'Log in' }}</span>
                <span>
                  {{ loading ? 'Logging in...' : 'Log in' }}
                  <ArrowRight :size="16" aria-hidden="true" />
                </span>
              </button>
            </form>

            <form v-else-if="authMode === 'register'" class="cc-login-form" @submit.prevent="handleRegister">
              <label class="cc-field" for="register-username">
                <span>Username</span>
                <input id="register-username" v-model="registerForm.username" autocomplete="username" placeholder="student username" />
              </label>
              <label class="cc-field" for="register-name">
                <span>Full name</span>
                <input id="register-name" v-model="registerForm.fullName" autocomplete="name" placeholder="Your full name" />
              </label>
              <label class="cc-field" for="register-email">
                <span>Email</span>
                <input id="register-email" v-model="registerForm.email" autocomplete="email" placeholder="you@example.com" />
              </label>
              <label class="cc-field" for="register-password">
                <span>Password</span>
                <input id="register-password" v-model="registerForm.password" type="password" autocomplete="new-password" placeholder="At least 8 characters" />
              </label>
              <button class="cc-motion-button" type="submit" :disabled="loading">
                <span>{{ loading ? 'Creating...' : 'Create account' }}</span>
                <span>{{ loading ? 'Creating...' : 'Create account' }}<ArrowRight :size="16" aria-hidden="true" /></span>
              </button>
            </form>

            <form v-else-if="authMode === 'forgot'" class="cc-login-form" @submit.prevent="handleForgotPassword">
              <label class="cc-field" for="forgot-account">
                <span>Username or email</span>
                <input id="forgot-account" v-model="forgotForm.usernameOrEmail" autocomplete="username" placeholder="student1 or email@example.com" />
              </label>
              <button class="cc-motion-button" type="submit" :disabled="loading">
                <span>{{ loading ? 'Generating...' : 'Generate reset token' }}</span>
                <span>{{ loading ? 'Generating...' : 'Generate reset token' }}<ArrowRight :size="16" aria-hidden="true" /></span>
              </button>
              <p v-if="forgotResult" class="cc-signup">{{ forgotResult }}</p>
            </form>

            <form v-else-if="authMode === 'public-submit'" class="cc-login-form" @submit.prevent="handlePublicSubmit">
              <label class="cc-field" for="public-category">
                <span>Anonymous category</span>
                <select id="public-category" v-model="publicForm.categoryId">
                  <option disabled value="">Choose a category</option>
                  <option v-for="category in publicCategories" :key="category.id" :value="category.id">{{ category.name }}</option>
                </select>
              </label>
              <label class="cc-field" for="public-title">
                <span>Title</span>
                <input id="public-title" v-model="publicForm.title" maxlength="160" placeholder="Brief case title" />
              </label>
              <label class="cc-field" for="public-description">
                <span>Description</span>
                <textarea id="public-description" v-model="publicForm.description" rows="4" placeholder="Describe what happened"></textarea>
              </label>
              <label class="cc-field" for="public-email">
                <span>Contact email</span>
                <input id="public-email" v-model="publicForm.publicContactEmail" autocomplete="email" placeholder="Optional" />
              </label>
              <label class="cc-field" for="public-files">
                <span>Evidence files</span>
                <input id="public-files" type="file" multiple @change="publicForm.files = Array.from($event.target.files || [])" />
              </label>
              <button class="cc-motion-button" type="submit" :disabled="loading">
                <span>{{ loading ? 'Submitting...' : 'Submit anonymously' }}</span>
                <span>{{ loading ? 'Submitting...' : 'Submit anonymously' }}<ArrowRight :size="16" aria-hidden="true" /></span>
              </button>
              <p v-if="publicTrackingCode" class="cc-signup">Case {{ trackForm.caseNumber }} · Tracking code {{ publicTrackingCode }}</p>
            </form>

            <form v-else class="cc-login-form" @submit.prevent="handlePublicTrack">
              <label class="cc-field" for="track-case">
                <span>Case number</span>
                <input id="track-case" v-model="trackForm.caseNumber" placeholder="SCF-..." />
              </label>
              <label class="cc-field" for="track-code">
                <span>Tracking code</span>
                <input id="track-code" v-model="trackForm.trackingCode" type="password" placeholder="Tracking code" />
              </label>
              <button class="cc-motion-button" type="submit" :disabled="loading">
                <span>{{ loading ? 'Tracking...' : 'Track case' }}</span>
                <span>{{ loading ? 'Tracking...' : 'Track case' }}<ArrowRight :size="16" aria-hidden="true" /></span>
              </button>
              <section v-if="trackedCase" class="public-result">
                <strong>{{ trackedCase.caseNumber }}</strong>
                <span>{{ trackedCase.title }} · {{ formatStatus(trackedCase.status) }}</span>
                <textarea v-model="publicMessage" rows="3" placeholder="Send anonymous follow-up information"></textarea>
                <button class="cc-motion-button" type="button" :disabled="loading" @click="handlePublicMessage">
                  <span>Send follow-up</span>
                  <span>Send follow-up<ArrowRight :size="16" aria-hidden="true" /></span>
                </button>
              </section>
            </form>

            <p v-if="authMode === 'login'" class="cc-signup">
              Don't have an account? <a href="#" @click.prevent="setAuthMode('register')">Sign Up</a>
              <br />
              <a href="#" @click.prevent="setAuthMode('public-submit')">Submit anonymously</a>
              ·
              <a href="#" @click.prevent="setAuthMode('public-track')">Track anonymous case</a>
            </p>
            <p v-else class="cc-signup">
              <a href="#" @click.prevent="setAuthMode('login')">Back to login</a>
            </p>
          </div>
        </section>
      </section>

      <section v-else class="workspace">
        <header class="topbar">
          <div>
            <span class="eyebrow">{{ currentUser.role.replace('_', ' ') }}</span>
            <h1>{{ pageTitle }}</h1>
          </div>
          <div class="top-actions">
            <el-popover placement="bottom-end" width="360" trigger="click">
              <template #reference>
                <el-badge :value="unreadNotifications" :hidden="unreadNotifications === 0" :max="99">
                  <el-button :icon="Bell">Notifications</el-button>
                </el-badge>
              </template>
              <div class="notification-list">
                <p v-if="!notifications.length" class="muted">No notifications yet.</p>
                <button
                  v-for="item in notifications"
                  :key="item.id"
                  type="button"
                  class="notification-item"
                  :class="{ unread: !item.readFlag, clickable: canOpenNotificationCase(item) }"
                  @click="handleNotificationClick(item)"
                >
                  <small v-if="item.caseNumber">Case {{ item.caseNumber }}</small>
                  <strong>{{ item.title }}</strong>
                  <span>{{ item.content }}</span>
                  <small>Email: {{ item.emailStatus }} · {{ item.readFlag ? 'Read' : 'Unread' }}</small>
                  <small v-if="item.failureReason">Reason: {{ item.failureReason }}</small>
                </button>
              </div>
            </el-popover>
            <el-tag :type="currentUser.status === 'ACTIVE' ? 'success' : 'danger'">{{ currentUser.status }}</el-tag>
          </div>
        </header>

        <section v-if="isOverviewView" class="metrics-grid">
          <InfoTile title="Total" :value="String(metrics.total)" label="Visible cases" />
          <InfoTile title="Open" :value="String(metrics.open)" label="Needs action or monitoring" />
          <InfoTile title="Overdue" :value="String(metrics.overdue)" label="Past SLA target" />
          <InfoTile title="High priority" :value="String(metrics.highPriority)" label="High or urgent cases" />
          <InfoTile title="Resolved" :value="String(metrics.resolved)" label="Awaiting closure or follow-up" />
          <InfoTile title="Follow-up" :value="String(metrics.followUps)" label="Student review requested" />
        </section>

        <section v-if="isCaseTableView" class="filter-panel">
          <el-input v-model="caseFilters.keyword" clearable placeholder="Search case no., title or keyword" />
          <el-select v-model="caseFilters.status" clearable placeholder="Status">
            <el-option v-for="status in statusOptions" :key="status" :label="formatStatus(status)" :value="status" />
          </el-select>
          <el-select v-model="caseFilters.priority" clearable placeholder="Priority">
            <el-option label="Low" value="LOW" />
            <el-option label="Normal" value="NORMAL" />
            <el-option label="High" value="HIGH" />
            <el-option label="Urgent" value="URGENT" />
          </el-select>
          <el-select v-model="caseFilters.overdue" clearable placeholder="SLA">
            <el-option label="Overdue" :value="true" />
            <el-option label="On track" :value="false" />
          </el-select>
        </section>

        <section v-if="view === 'student-overview'" class="dashboard-panels">
          <section class="panel table-panel">
            <div class="panel-header">
              <div><h2>Recent cases</h2><span class="muted">Your latest case activity.</span></div>
              <el-button @click="selectView('my-cases')">Open My Cases</el-button>
            </div>
            <CaseTable compact title="Recent cases" :cases="recentVisibleCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
          </section>
          <section class="panel quick-panel">
            <div class="panel-header"><h2>Quick actions</h2><FilePlus :size="22" /></div>
            <el-button type="primary" class="wide" @click="selectView('submit-case')">Submit a new case</el-button>
            <el-button class="wide" @click="selectView('anonymous-tracking')">Track anonymous case</el-button>
          </section>
        </section>

        <section v-if="view === 'submit-case'" class="form-page">
          <section class="panel">
            <div class="panel-header">
              <div>
                <span class="eyebrow">New case</span>
                <h2>Submit complaint or feedback</h2>
              </div>
              <FilePlus :size="22" aria-hidden="true" />
            </div>
            <el-form label-position="top" @submit.prevent>
              <el-form-item label="Category">
                <el-select v-model="caseForm.categoryId" class="wide" placeholder="Select category">
                  <el-option
                    v-for="category in enabledCategories"
                    :key="category.id"
                    :label="category.name"
                    :value="category.id"
                  />
                </el-select>
              </el-form-item>
              <el-form-item label="Title"><el-input v-model="caseForm.title" maxlength="160" show-word-limit /></el-form-item>
              <el-form-item label="Description"><el-input v-model="caseForm.description" type="textarea" :rows="5" /></el-form-item>
              <el-form-item label="Priority">
                <el-select v-model="caseForm.priority" class="wide">
                  <el-option label="Low" value="LOW" />
                  <el-option label="Normal" value="NORMAL" />
                  <el-option label="High" value="HIGH" />
                  <el-option label="Urgent" value="URGENT" />
                </el-select>
              </el-form-item>
              <el-form-item label="Identity option">
                <div class="anonymous-choice" role="group" aria-label="Choose submission identity">
                  <el-button
                    :type="!caseForm.anonymous ? 'primary' : 'default'"
                    @click="setAnonymousPreference(false)"
                  >
                    Use my identity
                  </el-button>
                  <el-button
                    :type="caseForm.anonymous ? 'warning' : 'default'"
                    :disabled="!selectedCategory?.anonymousAllowed"
                    @click="setAnonymousPreference(true)"
                  >
                    Submit anonymously
                  </el-button>
                </div>
                <span v-if="selectedCategory?.anonymousAllowed" class="helper">This category allows anonymous submission under the configured policy.</span>
                <span v-else-if="selectedCategory" class="helper">This category requires identity under policy.</span>
                <span v-else class="helper">Select a category to see whether anonymous submission is allowed.</span>
              </el-form-item>
              <el-form-item label="Evidence files">
                <el-upload v-model:file-list="caseForm.files" drag multiple :auto-upload="false">
                  <UploadCloud :size="26" />
                  <div class="upload-text">Drop documents or images here, or click to choose</div>
                </el-upload>
              </el-form-item>
              <el-button type="primary" :loading="loading" class="wide" @click="handleSubmitCase">Submit case</el-button>
            </el-form>
          </section>
        </section>

        <section v-if="view === 'my-cases'" class="full-page-table">
          <CaseTable title="My cases" :cases="filteredCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
        </section>

        <section v-if="view === 'anonymous-tracking'" class="form-page">
          <section class="panel">
            <div class="panel-header"><h2>Track anonymous case</h2><ShieldCheck :size="22" /></div>
            <el-form label-position="top" @submit.prevent>
              <el-form-item label="Case number"><el-input v-model="trackForm.caseNumber" /></el-form-item>
              <el-form-item label="Tracking code"><el-input v-model="trackForm.trackingCode" show-password /></el-form-item>
              <el-button type="primary" :loading="loading" class="wide" @click="handlePublicTrack">Track case</el-button>
            </el-form>
            <section v-if="trackedCase" class="public-result">
              <div class="case-title-row">
                <div>
                  <strong>{{ trackedCase.caseNumber }}</strong>
                  <span class="muted">{{ trackedCase.title }}</span>
                </div>
                <el-tag :type="statusTone(trackedCase.status)">{{ formatStatus(trackedCase.status) }}</el-tag>
              </div>
              <p>{{ trackedCase.description }}</p>
              <el-input v-model="publicMessage" type="textarea" :rows="3" placeholder="Send anonymous follow-up information" />
              <el-button :loading="loading" @click="handlePublicMessage">Send follow-up</el-button>
            </section>
          </section>
        </section>

        <section v-if="view === 'officer-overview'" class="dashboard-panels">
          <section class="panel table-panel">
            <div class="panel-header"><h2>Latest activity</h2><ClipboardList :size="22" /></div>
            <CaseTable compact title="Latest activity" :cases="recentVisibleCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
          </section>
          <section class="panel quick-panel">
            <div class="panel-header"><h2>Queues</h2><ClipboardList :size="22" /></div>
            <el-button type="primary" class="wide" @click="selectView('review-queue')">Review Queue</el-button>
            <el-button class="wide" @click="selectView('overdue-cases')">Overdue Cases</el-button>
          </section>
        </section>

        <section v-if="view === 'review-queue'" class="full-page-table">
          <CaseTable title="Officer review queue" :cases="filteredCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
        </section>

        <section v-if="view === 'department-overview'" class="dashboard-panels">
          <section class="panel table-panel">
            <div class="panel-header"><h2>Department activity</h2><ClipboardList :size="22" /></div>
            <CaseTable compact title="Department activity" :cases="recentVisibleCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
          </section>
          <section class="panel quick-panel">
            <div class="panel-header"><h2>Queues</h2><Building2 :size="22" /></div>
            <el-button type="primary" class="wide" @click="selectView('department-cases')">My Department Cases</el-button>
            <el-button class="wide" @click="selectView('overdue-cases')">Overdue Cases</el-button>
            <el-button class="wide" @click="selectView('internal-notes')">Internal Notes</el-button>
          </section>
        </section>

        <section v-if="view === 'department-cases'" class="full-page-table">
          <CaseTable title="Assigned department cases" :cases="filteredCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
        </section>

        <section v-if="view === 'internal-notes'" class="full-page-table">
          <section class="panel table-panel">
            <div class="panel-header">
              <div><h2>Internal notes entry</h2><span class="muted">Open a department case to add investigation notes, tasks, or root cause analysis.</span></div>
              <el-button @click="selectView('department-cases')">Open Department Cases</el-button>
            </div>
            <CaseTable title="Cases with internal notes" :cases="filteredCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
          </section>
        </section>

        <section v-if="view === 'admin-overview'" class="dashboard-panels">
          <section class="panel table-panel">
            <div class="panel-header">
              <div><h2>Recent cases</h2><span class="muted">Latest system-wide complaint activity.</span></div>
              <el-button @click="selectView('all-cases')">Open All Cases</el-button>
            </div>
            <CaseTable compact title="Recent cases" :cases="recentVisibleCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
          </section>
          <section class="panel quick-panel">
            <div class="panel-header"><h2>System health</h2><Settings :size="22" /></div>
            <InfoTile title="Users" :value="String(users.length)" label="Accounts in system" />
            <InfoTile title="Departments" :value="String(departments.length)" label="Routing teams" />
            <InfoTile title="Categories" :value="String(categories.length)" label="Complaint types" />
          </section>
        </section>

        <section v-if="view === 'users'" class="workspace-page">
          <section class="content-grid users-layout">
            <section class="panel">
              <div class="panel-header"><h2>Create staff user</h2><UserCog :size="22" /></div>
              <el-form label-position="top" @submit.prevent>
                <el-form-item label="Username"><el-input v-model="staffForm.username" /></el-form-item>
                <el-form-item label="Full name"><el-input v-model="staffForm.fullName" /></el-form-item>
                <el-form-item label="Email"><el-input v-model="staffForm.email" /></el-form-item>
                <el-form-item label="Password"><el-input v-model="staffForm.password" type="password" show-password /></el-form-item>
                <el-form-item label="Role">
                  <el-select v-model="staffForm.role" class="wide">
                    <el-option label="Officer" value="OFFICER" />
                    <el-option label="Department Staff" value="DEPARTMENT_STAFF" />
                    <el-option label="Admin" value="ADMIN" />
                  </el-select>
                </el-form-item>
                <el-form-item v-if="staffForm.role === 'DEPARTMENT_STAFF'" label="Department">
                  <el-select v-model="staffForm.departmentId" class="wide">
                    <el-option v-for="department in departments" :key="department.id" :label="department.name" :value="department.id" />
                  </el-select>
                </el-form-item>
                <el-button type="primary" :loading="loading" class="wide" @click="handleCreateStaff">Create user</el-button>
              </el-form>
            </section>

            <section class="panel table-panel">
              <div class="panel-header"><h2>Accounts</h2><Users :size="22" /></div>
              <el-table :data="users" height="620">
                <el-table-column prop="username" label="Username" min-width="120" />
                <el-table-column prop="fullName" label="Name" min-width="150" />
                <el-table-column prop="email" label="Email" min-width="190" />
                <el-table-column prop="role" label="Role" min-width="150" />
                <el-table-column prop="departmentName" label="Department" min-width="150" />
                <el-table-column label="Active" min-width="110">
                  <template #default="{ row }">
                    <el-switch :model-value="row.status === 'ACTIVE'" @change="toggleUser(row)" />
                  </template>
                </el-table-column>
              </el-table>
            </section>
          </section>
        </section>

        <section v-if="view === 'reports'" class="full-page-table">
          <section class="panel table-panel">
            <div class="panel-header">
              <div><h2>Weekly reports</h2><span class="muted">Volume, status, departments, follow-ups and resolution time.</span></div>
              <div v-if="currentUser?.role === 'ADMIN'" class="button-row">
                <el-button type="primary" :loading="loading" @click="handleGenerateReport">Generate report</el-button>
                <el-button :loading="loading" @click="handleExportReports">Export CSV</el-button>
              </div>
            </div>
            <el-table :data="reports" height="620">
              <el-table-column label="Week" min-width="190">
                <template #default="{ row }">{{ row.weekStart }} - {{ row.weekEnd }}</template>
              </el-table-column>
              <el-table-column label="Total" min-width="90">
                <template #default="{ row }">{{ row.statistics.totalCases }}</template>
              </el-table-column>
              <el-table-column label="Follow-ups" min-width="110">
                <template #default="{ row }">{{ row.statistics.followUpCases }}</template>
              </el-table-column>
              <el-table-column label="Avg hours" min-width="120">
                <template #default="{ row }">{{ Number(row.statistics.averageResolutionHours || 0).toFixed(1) }}</template>
              </el-table-column>
              <el-table-column prop="generatedBy" label="Generated by" min-width="150" />
            </el-table>
          </section>
        </section>

        <section v-if="view === 'audit-logs'" class="full-page-table">
          <section class="panel table-panel">
            <div class="panel-header"><h2>Audit logs</h2><ClipboardList :size="22" /></div>
            <el-table :data="auditLogs" height="620">
              <el-table-column prop="action" label="Action" min-width="150" />
              <el-table-column prop="actorName" label="Actor" min-width="150" />
              <el-table-column prop="targetId" label="Target" min-width="150" />
              <el-table-column prop="detail" label="Detail" min-width="260" />
              <el-table-column label="Time" min-width="190">
                <template #default="{ row }">{{ dateTime(row.createdAt) }}</template>
              </el-table-column>
            </el-table>
          </section>
        </section>

        <section v-if="view === 'all-cases'" class="full-page-table">
          <CaseTable title="All cases" :cases="filteredCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
        </section>

        <section v-if="view === 'overdue-cases'" class="full-page-table">
          <CaseTable title="Overdue cases" :cases="overdueCases" :view-name="view" :role="currentUser.role" :loading="loading" @select="openCase" />
        </section>

        <section v-if="view === 'settings'" class="workspace-page">
          <section class="panel settings-panel">
            <el-tabs v-model="settingsTab">
              <el-tab-pane label="Departments" name="departments">
                <section class="settings-grid">
                  <section>
                    <div class="panel-header"><h2>Add department</h2><Building2 :size="22" /></div>
                    <el-form label-position="top" @submit.prevent>
                      <el-form-item label="Name"><el-input v-model="departmentForm.name" /></el-form-item>
                      <el-form-item label="Description"><el-input v-model="departmentForm.description" type="textarea" :rows="3" /></el-form-item>
                      <el-checkbox v-model="departmentForm.enabled">Enabled</el-checkbox>
                      <el-button type="primary" :loading="loading" class="wide stack-action" @click="handleCreateDepartment">Add department</el-button>
                    </el-form>
                  </section>
                  <section class="table-panel">
                    <div class="panel-header"><h2>Departments</h2><Building2 :size="22" /></div>
                    <el-table :data="departments" height="420">
                      <el-table-column prop="name" label="Name" min-width="150" />
                      <el-table-column prop="description" label="Description" min-width="240" />
                      <el-table-column label="Status" min-width="110">
                        <template #default="{ row }"><el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? 'Enabled' : 'Disabled' }}</el-tag></template>
                      </el-table-column>
                    </el-table>
                  </section>
                </section>
              </el-tab-pane>
              <el-tab-pane label="Categories & SLA" name="categories">
                <section class="settings-grid">
                  <section>
                    <div class="panel-header"><h2>Add category</h2><ListTree :size="22" /></div>
                    <el-form label-position="top" @submit.prevent>
                      <el-form-item label="Name"><el-input v-model="categoryForm.name" /></el-form-item>
                      <el-form-item label="Description"><el-input v-model="categoryForm.description" type="textarea" :rows="3" /></el-form-item>
                      <el-form-item label="Default department">
                        <el-select v-model="categoryForm.defaultDepartmentId" class="wide">
                          <el-option v-for="department in departments" :key="department.id" :label="department.name" :value="department.id" />
                        </el-select>
                      </el-form-item>
                      <el-form-item label="Default SLA hours"><el-input-number v-model="categoryForm.defaultSlaHours" :min="1" :max="720" class="wide" /></el-form-item>
                      <el-form-item label="Workflow">
                        <el-select v-model="categoryForm.workflowTemplate" class="wide">
                          <el-option label="Standard" value="STANDARD" />
                          <el-option label="Fast track" value="FAST_TRACK" />
                          <el-option label="Sensitive" value="SENSITIVE" />
                          <el-option label="Crisis" value="CRISIS" />
                        </el-select>
                      </el-form-item>
                      <el-checkbox v-model="categoryForm.anonymousAllowed">Allow anonymous submissions</el-checkbox>
                      <el-checkbox v-model="categoryForm.enabled">Enabled</el-checkbox>
                      <el-button type="primary" :loading="loading" class="wide stack-action" @click="handleCreateCategory">Add category</el-button>
                    </el-form>
                  </section>
                  <section class="table-panel">
                    <div class="panel-header"><h2>Categories</h2><ListTree :size="22" /></div>
                    <el-table :data="categories" height="420">
                      <el-table-column prop="name" label="Name" min-width="150" />
                      <el-table-column label="Anonymous" min-width="120">
                        <template #default="{ row }"><el-tag :type="row.anonymousAllowed ? 'warning' : 'info'">{{ row.anonymousAllowed ? 'Allowed' : 'No' }}</el-tag></template>
                      </el-table-column>
                      <el-table-column prop="defaultDepartmentName" label="Default department" min-width="170" />
                      <el-table-column prop="defaultSlaHours" label="SLA hours" min-width="110" />
                      <el-table-column prop="workflowTemplate" label="Workflow" min-width="140" />
                    </el-table>
                  </section>
                </section>
              </el-tab-pane>
              <el-tab-pane label="Email Settings" name="email">
                <section class="settings-grid">
                  <section>
                    <div class="panel-header"><h2>SMTP configuration</h2><Mail :size="22" /></div>
                    <el-alert
                      v-if="!emailSettings.complete"
                      type="warning"
                      :closable="false"
                      show-icon
                      title="Email is not fully configured. In-app notifications still work."
                    />
                    <el-form label-position="top" @submit.prevent>
                      <el-checkbox v-model="emailForm.enabled">Enable email sending</el-checkbox>
                      <el-form-item label="SMTP host"><el-input v-model="emailForm.host" placeholder="smtp.example.com" /></el-form-item>
                      <el-form-item label="Port"><el-input-number v-model="emailForm.port" :min="1" :max="65535" class="wide" /></el-form-item>
                      <el-form-item label="Username"><el-input v-model="emailForm.username" autocomplete="username" /></el-form-item>
                      <el-form-item label="Password"><el-input v-model="emailForm.password" type="password" show-password autocomplete="new-password" placeholder="Leave blank to keep existing" /></el-form-item>
                      <el-form-item label="From address"><el-input v-model="emailForm.fromAddress" /></el-form-item>
                      <el-checkbox v-model="emailForm.startTls">Use STARTTLS</el-checkbox>
                      <el-button type="primary" :loading="loading" class="wide stack-action" @click="handleSaveEmailSettings">Save email settings</el-button>
                    </el-form>
                  </section>
                  <section>
                    <div class="panel-header"><h2>Send test email</h2><Mail :size="22" /></div>
                    <div class="status-list">
                      <span>Enabled: <strong>{{ emailSettings.enabled ? 'Yes' : 'No' }}</strong></span>
                      <span>Complete: <strong>{{ emailSettings.complete ? 'Yes' : 'No' }}</strong></span>
                      <span>Host: <strong>{{ emailSettings.host || 'Not configured' }}</strong></span>
                      <span>Username: <strong>{{ emailSettings.username || 'Not configured' }}</strong></span>
                      <span>Password saved: <strong>{{ emailSettings.passwordConfigured ? 'Yes' : 'No' }}</strong></span>
                    </div>
                    <el-form label-position="top" @submit.prevent>
                      <el-form-item label="Recipient email"><el-input v-model="testEmailTo" /></el-form-item>
                      <el-button :loading="loading" class="wide" @click="handleSendTestEmail">Send Test Email</el-button>
                    </el-form>
                    <el-alert v-if="testEmailResult" :type="testEmailResult.sent ? 'success' : 'error'" :closable="false" show-icon :title="testEmailResult.message" />
                  </section>
                </section>
              </el-tab-pane>
            </el-tabs>
          </section>
        </section>

        <section v-if="view === 'profile-password'" class="form-page">
          <section class="panel">
            <div class="panel-header"><h2>Profile & Password</h2><UserCog :size="22" /></div>
            <el-form label-position="top" @submit.prevent>
              <el-form-item label="Username"><el-input :model-value="profileForm.username" disabled /></el-form-item>
              <el-form-item label="Full name"><el-input v-model="profileForm.fullName" /></el-form-item>
              <el-form-item label="Email"><el-input v-model="profileForm.email" /></el-form-item>
              <el-button type="primary" :loading="loading" class="wide" @click="handleSaveProfile">Save profile</el-button>
            </el-form>
            <div class="divider"></div>
            <el-form label-position="top" @submit.prevent>
              <el-form-item label="Old password"><el-input v-model="passwordForm.oldPassword" type="password" show-password autocomplete="current-password" /></el-form-item>
              <el-form-item label="New password"><el-input v-model="passwordForm.newPassword" type="password" show-password autocomplete="new-password" /></el-form-item>
              <el-form-item label="Confirm new password"><el-input v-model="passwordForm.confirmPassword" type="password" show-password autocomplete="new-password" /></el-form-item>
              <el-button :loading="loading" class="wide" @click="handleChangePassword">Change password</el-button>
            </el-form>
          </section>
        </section>
      </section>
    </main>

    <el-drawer v-model="detailOpen" size="560px" :title="selectedCase?.caseNumber || 'Case detail'">
      <section v-if="selectedCase" class="drawer-body">
        <div class="case-title-row">
          <div>
            <h2>{{ selectedCase.title }}</h2>
            <span class="muted">{{ selectedCase.categoryName }} · {{ selectedCase.submittedByName }}</span>
          </div>
          <el-tag :type="statusTone(selectedCase.status)">{{ formatStatus(selectedCase.status) }}</el-tag>
        </div>
        <p class="description">{{ selectedCase.description }}</p>

        <div class="mini-grid">
          <InfoTile title="Submitted" :value="dateOnly(selectedCase.submittedAt)" label="Original creation date" />
          <InfoTile title="Department" :value="selectedCase.assignedDepartmentName || 'Unassigned'" label="Routing destination" />
          <InfoTile title="Priority" :value="selectedCase.priority || 'NORMAL'" :label="formatDueState(selectedCase)" />
          <InfoTile title="Due" :value="dateOnly(selectedCase.dueAt)" :label="selectedCase.overdue ? 'SLA overdue' : 'SLA target'" />
        </div>

        <section v-if="selectedCase.assignments?.length" class="drawer-section">
          <h3>Departments</h3>
          <el-tag v-for="assignment in selectedCase.assignments" :key="assignment.id" class="attachment-tag" :type="assignment.assignmentRole === 'LEAD' ? 'success' : 'info'">
            {{ assignment.assignmentRole }} · {{ assignment.departmentName }}
          </el-tag>
        </section>

        <section class="drawer-section">
          <h3>Messages</h3>
          <p v-if="!selectedCase.messages.length" class="muted">No messages yet.</p>
          <article v-for="message in selectedCase.messages" :key="message.id" class="timeline-item">
            <strong>{{ message.senderName }} · {{ formatStatus(message.type) }}</strong>
            <span>{{ message.content }}</span>
            <small>{{ dateTime(message.createdAt) }}</small>
          </article>
        </section>

        <section class="drawer-section">
          <h3>Status history</h3>
          <article v-for="log in selectedCase.statusLogs" :key="log.id" class="timeline-item">
            <strong>{{ formatStatus(log.newStatus) }}</strong>
            <span>{{ log.note }}</span>
            <small>{{ log.operatorName }} · {{ dateTime(log.createdAt) }}</small>
          </article>
        </section>

        <section class="drawer-section" v-if="selectedCase.attachments.length">
          <h3>Attachments</h3>
          <a
            v-for="file in selectedCase.attachments"
            :key="file.id"
            class="attachment-link"
            :href="attachmentDownloadUrl(selectedCase.id, file.id)"
            target="_blank"
            rel="noreferrer"
          >
            {{ file.previewable ? 'Preview/download' : 'Download' }} · {{ file.originalFileName }}
          </a>
        </section>

        <section v-if="currentUser?.role === 'STUDENT'" class="drawer-actions">
          <el-input v-model="studentMessage" type="textarea" :rows="3" placeholder="Add requested information or a note" />
          <el-button :loading="loading" @click="handleStudentMessage">Send information</el-button>
          <el-input v-model="followUpMessage" type="textarea" :rows="3" placeholder="Explain why you need a follow-up review" />
          <el-button type="warning" :loading="loading" :disabled="selectedCase.status !== 'RESOLVED'" @click="handleFollowUp">Request follow-up</el-button>
          <el-rate v-model="satisfactionForm.rating" :max="5" />
          <el-input v-model="satisfactionForm.comment" type="textarea" :rows="2" placeholder="Optional satisfaction comment" />
          <el-button :loading="loading" :disabled="!['RESOLVED', 'CLOSED'].includes(selectedCase.status)" @click="handleSatisfaction">Submit satisfaction</el-button>
          <el-input v-model="reopenMessage" type="textarea" :rows="2" placeholder="Why should this case be reopened?" />
          <el-button type="danger" :loading="loading" :disabled="!['RESOLVED', 'CLOSED'].includes(selectedCase.status)" @click="handleReopen">Request reopen</el-button>
        </section>

        <section v-if="currentUser?.role === 'OFFICER'" class="drawer-actions">
          <el-select v-model="assignForm.departmentIds" class="wide" multiple placeholder="Assign one or more departments">
            <el-option v-for="department in departments" :key="department.id" :label="department.name" :value="department.id" />
          </el-select>
          <el-input v-model="assignForm.note" placeholder="Assignment note" />
          <el-button type="primary" :loading="loading" @click="handleAssign">Assign case</el-button>
          <el-input v-model="officerNote" type="textarea" :rows="3" placeholder="Request more information from student" />
          <el-button :loading="loading" @click="handleRequestInfo">Request information</el-button>
          <el-input v-model="closeNote" type="textarea" :rows="3" placeholder="Closure note" />
          <el-button type="success" :loading="loading" @click="handleCloseCase">Close case</el-button>
          <el-input v-model="reminderNote" placeholder="Reminder or escalation note" />
          <el-button type="warning" :loading="loading" @click="handleReminder">Send reminder / escalation</el-button>
        </section>

        <section v-if="currentUser?.role === 'DEPARTMENT_STAFF'" class="drawer-actions">
          <el-input v-model="departmentNote" type="textarea" :rows="3" placeholder="Progress update" />
          <el-button :loading="loading" @click="handleProgress">Update progress</el-button>
          <el-input v-model="resolveNote" type="textarea" :rows="3" placeholder="Final response to student" />
          <el-button type="success" :loading="loading" @click="handleResolve">Mark resolved</el-button>
        </section>

        <section v-if="currentUser?.role !== 'STUDENT'" class="drawer-section">
          <h3>Internal notes</h3>
          <p v-if="!privateNotes.length" class="muted">No internal notes yet.</p>
          <article v-for="note in privateNotes" :key="note.id" class="timeline-item internal">
            <strong>{{ note.authorName }}</strong>
            <span>{{ note.content }}</span>
            <small>{{ note.rootCause || 'No root cause yet' }} · {{ dateTime(note.createdAt) }}</small>
          </article>
          <el-input v-model="privateNoteForm.content" type="textarea" :rows="3" placeholder="Staff-only investigation note" />
          <el-input v-model="privateNoteForm.rootCause" placeholder="Root cause / task note" />
          <el-button :loading="loading" @click="handlePrivateNote">Add internal note</el-button>
        </section>
      </section>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, defineComponent, h, onBeforeUnmount, onMounted, reactive, ref, resolveComponent, watch } from 'vue';
import { ElMessage } from 'element-plus';
import {
  Bell,
  ArrowRight,
  BarChart3,
  Building2,
  ClipboardList,
  Eye,
  EyeOff,
  FilePlus,
  LayoutDashboard,
  ListTree,
  LogOut,
  Mail,
  Settings,
  ShieldCheck,
  UploadCloud,
  UserCog,
  Users
} from 'lucide-vue-next';
import {
  assignCase,
  addPrivateNote,
  attachmentDownloadUrl,
  closeCase,
  createCategory,
  createDepartment,
  createReminder,
  createStaffUser,
  exportReports,
  forgotPassword,
  changePassword,
  getAuditLogs,
  getCaseDetail,
  getPrivateNotes,
  getDashboardSummary,
  getEmailSettings,
  generateWeeklyReport,
  getCategories,
  getDepartmentCase,
  getDepartmentCases,
  getDepartments,
  getNotifications,
  getOfficerCase,
  getOfficerCases,
  getOverdueCases,
  getProfile,
  getStudentCase,
  getStudentCases,
  getUsers,
  getWeeklyReports,
  login,
  markNotificationRead,
  me,
  requestReopen,
  registerStudent,
  requestCaseInfo,
  requestFollowUp,
  resolveCase,
  searchCases,
  sendTestEmail,
  sendStudentMessage,
  sendPublicMessage,
  submitPublicCase,
  submitSatisfaction,
  submitStudentCase,
  trackPublicCase,
  updateCaseProgress,
  updateEmailSettings,
  updateProfile,
  updateUser
} from './api';
import { authModeConfig, brandName } from './authPresentation';
import { formatDueState, priorityTone } from './advancedPresentation';
import { formatStatus, statusTone } from './casePresentation';
import { dashboardMetrics, navForRole, recentCases } from './dashboardPresentation';
import { canOpenNotificationCase, unreadNotificationCount } from './notificationPresentation';
import { roleHome } from './roleHome';
import { emptyStateForView } from './uiPresentation';

const InfoTile = defineComponent({
  props: { title: String, value: String, label: String },
  setup(props) {
    return () => h('article', { class: 'tile' }, [
      h('span', props.title),
      h('strong', props.value),
      h('small', props.label)
    ]);
  }
});

const CaseTable = defineComponent({
  props: { title: String, cases: Array, compact: Boolean, viewName: String, role: String, loading: Boolean },
  emits: ['select'],
  setup(props, { emit }) {
    const table = () => h(resolveComponent('el-table'), { data: props.cases || [], height: props.compact ? 300 : 540, onRowClick: (row) => emit('select', row) }, {
      default: () => [
        h(resolveComponent('el-table-column'), { prop: 'caseNumber', label: 'Case no.', minWidth: 150 }),
        h(resolveComponent('el-table-column'), { prop: 'title', label: 'Title', minWidth: 180 }),
        h(resolveComponent('el-table-column'), { prop: 'categoryName', label: 'Category', minWidth: 150 }),
        h(resolveComponent('el-table-column'), { label: 'Status', minWidth: 160 }, {
          default: ({ row }) => h(resolveComponent('el-tag'), { type: statusTone(row.status) }, () => formatStatus(row.status))
        }),
        h(resolveComponent('el-table-column'), { label: 'Priority', minWidth: 120 }, {
          default: ({ row }) => h(resolveComponent('el-tag'), { type: priorityTone(row.priority) }, () => row.priority || 'NORMAL')
        }),
        h(resolveComponent('el-table-column'), { label: 'SLA', minWidth: 130 }, {
          default: ({ row }) => h(resolveComponent('el-tag'), { type: row.overdue ? 'danger' : 'success' }, () => formatDueState(row))
        }),
        h(resolveComponent('el-table-column'), { prop: 'assignedDepartmentName', label: 'Department', minWidth: 160 }),
        h(resolveComponent('el-table-column'), { label: 'Updated', minWidth: 140 }, {
          default: ({ row }) => dateOnly(row.updatedAt)
        })
      ]
    });

    const emptyState = () => {
      const state = emptyStateForView(props.viewName, props.role);
      return h('div', { class: ['empty-state', `empty-state-${state.tone}`] }, [
        h('span', { class: 'empty-mark', 'aria-hidden': 'true' }, '*'),
        h('strong', state.title),
        h('p', state.message),
        h('small', state.action)
      ]);
    };

    const content = () => {
      if (props.loading) {
        return h('div', { class: 'table-skeleton', 'aria-label': 'Loading cases' }, [
          h('span'),
          h('span'),
          h('span'),
          h('span')
        ]);
      }
      if (!(props.cases || []).length) return emptyState();
      return table();
    };

    if (props.compact) {
      return () => content();
    }

    return () => h('section', { class: 'panel table-panel' }, [
      h('div', { class: 'panel-header' }, [
        h('h2', props.title),
        h(ClipboardList, { size: 22 })
      ]),
      content()
    ]);
  }
});

const authMode = ref('login');
const view = ref('login');
const loading = ref(false);
const showPassword = ref(false);
const rememberLogin = ref(false);
const authTyping = ref(false);
const typingReaction = ref(false);
const purpleBlinking = ref(false);
const charcoalBlinking = ref(false);
const passwordGlance = ref(false);
const purpleBlock = ref(null);
const charcoalBlock = ref(null);
const coralBlock = ref(null);
const yellowBlock = ref(null);
const currentUser = ref(null);
const users = ref([]);
const departments = ref([]);
const categories = ref([]);
const cases = ref([]);
const reports = ref([]);
const notifications = ref([]);
const auditLogs = ref([]);
const selectedCase = ref(null);
const detailOpen = ref(false);
const privateNotes = ref([]);

const loginForm = reactive({ username: '', password: '' });
const registerForm = reactive({ username: '', password: '', fullName: '', email: '' });
const forgotForm = reactive({ usernameOrEmail: '' });
const forgotResult = ref('');
const caseForm = reactive({ categoryId: null, title: '', description: '', anonymous: false, priority: 'NORMAL', files: [] });
const publicForm = reactive({ categoryId: null, publicSubmitterType: 'Student', publicContactEmail: '', priority: 'NORMAL', title: '', description: '', files: [] });
const trackForm = reactive({ caseNumber: '', trackingCode: '' });
const trackedCase = ref(null);
const publicTrackingCode = ref('');
const publicMessage = ref('');
const staffForm = reactive({ username: '', password: 'Staff123!', fullName: '', email: '', role: 'DEPARTMENT_STAFF', departmentId: null });
const departmentForm = reactive({ name: '', description: '', enabled: true });
const categoryForm = reactive({ name: '', description: '', anonymousAllowed: false, enabled: true, defaultDepartmentId: null, defaultSlaHours: 72, workflowTemplate: 'STANDARD' });
const assignForm = reactive({ departmentId: null, departmentIds: [], note: '' });
const studentMessage = ref('');
const followUpMessage = ref('');
const reopenMessage = ref('');
const officerNote = ref('');
const closeNote = ref('');
const reminderNote = ref('');
const departmentNote = ref('');
const resolveNote = ref('');
const satisfactionForm = reactive({ rating: 5, comment: '' });
const privateNoteForm = reactive({ content: '', rootCause: '' });
const caseFilters = reactive({ keyword: '', status: '', priority: '', overdue: null });
const statusOptions = ['SUBMITTED', 'UNDER_REVIEW', 'AWAITING_STUDENT_INFO', 'ASSIGNED', 'IN_PROGRESS', 'URGENT_REVIEW', 'RESOLVED', 'FOLLOW_UP_REQUESTED', 'REOPEN_REQUESTED', 'REOPENED', 'CLOSED'];
const settingsTab = ref('departments');
const profileForm = reactive({ username: '', fullName: '', email: '', role: '', status: '' });
const passwordForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' });
const emailSettings = reactive({ enabled: false, host: '', port: 1025, username: '', passwordConfigured: false, fromAddress: '', startTls: false, complete: false });
const emailForm = reactive({ enabled: false, host: '', port: 1025, username: '', password: '', fromAddress: '', startTls: false });
const testEmailTo = ref('');
const testEmailResult = ref(null);
let notificationTimer = null;
let typingReactionTimer = null;
let purpleBlinkTimer = null;
let charcoalBlinkTimer = null;
let passwordGlanceTimer = null;

const authPointer = reactive({
  x: 0,
  y: 0,
  panelX: 0,
  panelY: 0,
  purpleFaceX: 0,
  purpleFaceY: 0,
  purpleSkew: 0,
  charcoalFaceX: 0,
  charcoalFaceY: 0,
  charcoalSkew: 0,
  coralFaceX: 0,
  coralFaceY: 0,
  coralSkew: 0,
  yellowFaceX: 0,
  yellowFaceY: 0,
  yellowSkew: 0
});
const passwordLength = computed(() => loginForm.password.length);
const currentAuthConfig = computed(() => authModeConfig(authMode.value));
const passwordVisibleWithText = computed(() => showPassword.value && passwordLength.value > 0);
const passwordHiddenWithText = computed(() => !showPassword.value && passwordLength.value > 0);
const authMotionClasses = computed(() => ({
  'is-email-focused': authTyping.value,
  'is-typing': typingReaction.value,
  'is-password-visible': passwordVisibleWithText.value,
  'is-password-hidden': passwordHiddenWithText.value,
  'is-purple-blinking': purpleBlinking.value,
  'is-charcoal-blinking': charcoalBlinking.value,
  'is-glancing': passwordGlance.value
}));
const authPointerStyle = computed(() => ({
  '--eye-x': `${authPointer.x}px`,
  '--eye-y': `${authPointer.y}px`,
  '--panel-x': `${authPointer.panelX * 10}px`,
  '--panel-y': `${authPointer.panelY * 6}px`,
  '--purple-face-x': `${authPointer.purpleFaceX}px`,
  '--purple-face-y': `${authPointer.purpleFaceY}px`,
  '--purple-skew': `${authPointer.purpleSkew}deg`,
  '--charcoal-face-x': `${authPointer.charcoalFaceX}px`,
  '--charcoal-face-y': `${authPointer.charcoalFaceY}px`,
  '--charcoal-skew': `${authPointer.charcoalSkew}deg`,
  '--coral-face-x': `${authPointer.coralFaceX}px`,
  '--coral-face-y': `${authPointer.coralFaceY}px`,
  '--coral-skew': `${authPointer.coralSkew}deg`,
  '--yellow-face-x': `${authPointer.yellowFaceX}px`,
  '--yellow-face-y': `${authPointer.yellowFaceY}px`,
  '--yellow-skew': `${authPointer.yellowSkew}deg`
}));

const homeView = computed(() => currentUser.value ? roleHome(currentUser.value.role) : 'login');
const enabledCategories = computed(() => categories.value.filter((item) => item.enabled));
const publicCategories = computed(() => enabledCategories.value.filter((item) => item.anonymousAllowed));
const selectedCategory = computed(() => categories.value.find((item) => item.id === caseForm.categoryId));
const metrics = computed(() => dashboardMetrics(cases.value));
const unreadNotifications = computed(() => unreadNotificationCount(notifications.value));
const isOverviewView = computed(() => ['student-overview', 'officer-overview', 'department-overview', 'admin-overview'].includes(view.value));
const isCaseTableView = computed(() => ['my-cases', 'review-queue', 'department-cases', 'all-cases', 'overdue-cases', 'internal-notes'].includes(view.value));
const filteredCases = computed(() => cases.value.filter((item) => {
  const keyword = caseFilters.keyword.trim().toLowerCase();
  const matchesKeyword = !keyword || [item.caseNumber, item.title, item.categoryName, item.assignedDepartmentName]
    .filter(Boolean)
    .join(' ')
    .toLowerCase()
    .includes(keyword);
  const matchesStatus = !caseFilters.status || item.status === caseFilters.status;
  const matchesPriority = !caseFilters.priority || item.priority === caseFilters.priority;
  const matchesOverdue = caseFilters.overdue === null || caseFilters.overdue === '' || item.overdue === caseFilters.overdue;
  return matchesKeyword && matchesStatus && matchesPriority && matchesOverdue;
}));
const recentVisibleCases = computed(() => recentCases(cases.value, 5));
const overdueCases = computed(() => filteredCases.value.filter((item) => item.overdue));

const navGroups = computed(() => {
  if (!currentUser.value) return [];
  return navForRole(currentUser.value.role).map((group) => ({
    ...group,
    items: group.items.map((item) => ({ ...item, icon: iconForView(item.view) }))
  }));
});

const pageTitle = computed(() => ({
  'student-overview': 'Student Overview',
  'submit-case': 'Submit Case',
  'my-cases': 'My Cases',
  'anonymous-tracking': 'Anonymous Tracking',
  'officer-overview': 'Officer Overview',
  'review-queue': 'Review Queue',
  'department-overview': 'Department Overview',
  'department-cases': 'My Department Cases',
  'internal-notes': 'Internal Notes',
  'admin-overview': 'Admin Overview',
  'all-cases': 'All Cases',
  'overdue-cases': 'Overdue Cases',
  users: 'Users',
  reports: 'Reports',
  'audit-logs': 'Audit Logs',
  settings: 'System Settings',
  'profile-password': 'Profile & Password'
})[view.value] || 'Dashboard');

function iconForView(viewName) {
  if (['my-cases', 'review-queue', 'department-cases', 'all-cases', 'overdue-cases', 'internal-notes'].includes(viewName)) return ClipboardList;
  if (viewName === 'submit-case') return FilePlus;
  if (viewName === 'anonymous-tracking') return ShieldCheck;
  if (viewName === 'users' || viewName === 'profile-password') return UserCog;
  if (viewName === 'reports') return BarChart3;
  if (viewName === 'audit-logs') return ShieldCheck;
  if (viewName === 'settings') return Settings;
  return LayoutDashboard;
}

function clamp(value, min, max) {
  return Math.max(min, Math.min(max, value));
}

function faceMotionFor(blockRef, mouseX, mouseY) {
  if (!blockRef.value) return { faceX: 0, faceY: 0, bodySkew: 0 };
  const rect = blockRef.value.getBoundingClientRect();
  const centerX = rect.left + rect.width / 2;
  const focusY = rect.top + rect.height / 3;
  const deltaX = mouseX - centerX;
  return {
    faceX: clamp(deltaX / 20, -15, 15),
    faceY: clamp((mouseY - focusY) / 30, -10, 10),
    bodySkew: clamp(-deltaX / 120, -6, 6)
  };
}

function applyBlockMotion(mouseX, mouseY) {
  const purple = faceMotionFor(purpleBlock, mouseX, mouseY);
  const charcoal = faceMotionFor(charcoalBlock, mouseX, mouseY);
  const coral = faceMotionFor(coralBlock, mouseX, mouseY);
  const yellow = faceMotionFor(yellowBlock, mouseX, mouseY);
  Object.assign(authPointer, {
    purpleFaceX: purple.faceX,
    purpleFaceY: purple.faceY,
    purpleSkew: purple.bodySkew,
    charcoalFaceX: charcoal.faceX,
    charcoalFaceY: charcoal.faceY,
    charcoalSkew: charcoal.bodySkew,
    coralFaceX: coral.faceX,
    coralFaceY: coral.faceY,
    coralSkew: coral.bodySkew,
    yellowFaceX: yellow.faceX,
    yellowFaceY: yellow.faceY,
    yellowSkew: yellow.bodySkew
  });
}

function handleAuthPointerMove(event) {
  const rect = event.currentTarget.getBoundingClientRect();
  const x = (event.clientX - rect.left - rect.width / 2) / rect.width;
  const y = (event.clientY - rect.top - rect.height / 2) / rect.height;
  authPointer.x = clamp(x * 10, -5, 5);
  authPointer.y = clamp(y * 10, -5, 5);
  authPointer.panelX = clamp(x, -0.5, 0.5);
  authPointer.panelY = clamp(y, -0.5, 0.5);
  applyBlockMotion(event.clientX, event.clientY);
}

function resetAuthPointer() {
  Object.assign(authPointer, {
    x: 0,
    y: 0,
    panelX: 0,
    panelY: 0,
    purpleFaceX: 0,
    purpleFaceY: 0,
    purpleSkew: 0,
    charcoalFaceX: 0,
    charcoalFaceY: 0,
    charcoalSkew: 0,
    coralFaceX: 0,
    coralFaceY: 0,
    coralSkew: 0,
    yellowFaceX: 0,
    yellowFaceY: 0,
    yellowSkew: 0
  });
}

function setAuthTyping(value) {
  authTyping.value = value;
}

function scheduleBlink(target, delay = 3000 + Math.random() * 4000) {
  return window.setTimeout(() => {
    target.value = true;
    window.setTimeout(() => {
      target.value = false;
      if (target === purpleBlinking) purpleBlinkTimer = scheduleBlink(purpleBlinking);
      if (target === charcoalBlinking) charcoalBlinkTimer = scheduleBlink(charcoalBlinking);
    }, 150);
  }, delay);
}

function schedulePasswordGlance(delay = 2000 + Math.random() * 3000) {
  return window.setTimeout(() => {
    if (passwordVisibleWithText.value) {
      passwordGlance.value = true;
      window.setTimeout(() => {
        passwordGlance.value = false;
      }, 800);
    }
    passwordGlanceTimer = schedulePasswordGlance();
  }, delay);
}

function startAuthMotionLoops() {
  purpleBlinkTimer = scheduleBlink(purpleBlinking);
  charcoalBlinkTimer = scheduleBlink(charcoalBlinking, 3500 + Math.random() * 3500);
  passwordGlanceTimer = schedulePasswordGlance();
}

function stopAuthMotionLoops() {
  [typingReactionTimer, purpleBlinkTimer, charcoalBlinkTimer, passwordGlanceTimer].forEach((timer) => {
    if (timer) window.clearTimeout(timer);
  });
}

async function selectView(nextView) {
  view.value = nextView;
  if (nextView === 'profile-password') await loadProfileData();
  if (nextView === 'settings') await loadSettingsData();
  if (['reports', 'users', 'audit-logs', 'admin-overview'].includes(nextView)) await loadAdminData();
  if (nextView === 'overdue-cases') await loadOverdueCases();
}

onMounted(async () => {
  startAuthMotionLoops();
  await loadReferences();
  const token = localStorage.getItem('scfs_token');
  if (!token) return;
  try {
    currentUser.value = await me();
    view.value = roleHome(currentUser.value.role);
    await refreshAll();
    startNotificationPolling();
  } catch {
    localStorage.removeItem('scfs_token');
  }
});

onBeforeUnmount(() => {
  stopNotificationPolling();
  stopAuthMotionLoops();
});

watch(authTyping, (value) => {
  if (!value) {
    window.clearTimeout(typingReactionTimer);
    typingReaction.value = false;
    return;
  }
  typingReaction.value = true;
  window.clearTimeout(typingReactionTimer);
  typingReactionTimer = window.setTimeout(() => {
    typingReaction.value = false;
  }, 800);
});

watch([passwordVisibleWithText, passwordGlance], ([visible]) => {
  if (!visible) passwordGlance.value = false;
});

async function handleLogin() {
  await runAction(async () => {
    activateSession(await login(loginForm));
    ElMessage.success('Login successful');
  });
}

async function handleRegister() {
  await runAction(async () => {
    activateSession(await registerStudent(registerForm));
    ElMessage.success('Student account created');
  });
}

async function handleForgotPassword() {
  await runAction(async () => {
    const response = await forgotPassword(forgotForm.usernameOrEmail);
    forgotResult.value = response.token
      ? `${response.message}. Token: ${response.token}`
      : response.message;
    ElMessage.success(response.message);
  });
}

function setAuthMode(mode) {
  authMode.value = mode;
  forgotResult.value = '';
  if (mode !== 'public-track') {
    trackedCase.value = null;
    publicMessage.value = '';
  }
}

async function handleSubmitCase() {
  await runAction(async () => {
    const detail = await submitStudentCase(caseForm);
    Object.assign(caseForm, { categoryId: null, title: '', description: '', anonymous: false, priority: 'NORMAL', files: [] });
    await refreshCases();
    selectedCase.value = detail;
    detailOpen.value = true;
    ElMessage.success('Case submitted');
  });
}

async function handlePublicSubmit() {
  await runAction(async () => {
    const response = await submitPublicCase(publicForm);
    publicTrackingCode.value = response.trackingCode;
    trackedCase.value = response.detail;
    trackForm.caseNumber = response.detail.caseNumber;
    trackForm.trackingCode = response.trackingCode;
    Object.assign(publicForm, { categoryId: null, publicSubmitterType: 'Student', publicContactEmail: '', priority: 'NORMAL', title: '', description: '', files: [] });
    authMode.value = 'public-track';
    ElMessage.success('Anonymous case submitted');
  });
}

async function handlePublicTrack() {
  await runAction(async () => {
    trackedCase.value = await trackPublicCase(trackForm);
    ElMessage.success('Case found');
  });
}

async function handlePublicMessage() {
  await runAction(async () => {
    trackedCase.value = await sendPublicMessage({ ...trackForm, content: publicMessage.value });
    publicMessage.value = '';
    ElMessage.success('Anonymous follow-up sent');
  });
}

async function handleCreateStaff() {
  await runAction(async () => {
    await createStaffUser({ ...staffForm, departmentId: staffForm.role === 'DEPARTMENT_STAFF' ? staffForm.departmentId : null });
    Object.assign(staffForm, { username: '', password: 'Staff123!', fullName: '', email: '', role: 'DEPARTMENT_STAFF', departmentId: null });
    await loadAdminData();
    ElMessage.success('User created');
  });
}

async function toggleUser(row) {
  await runAction(async () => {
    await updateUser(row.id, {
      fullName: row.fullName,
      email: row.email,
      status: row.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE',
      departmentId: row.departmentId
    });
    await loadAdminData();
  });
}

async function handleCreateDepartment() {
  await runAction(async () => {
    await createDepartment(departmentForm);
    Object.assign(departmentForm, { name: '', description: '', enabled: true });
    await loadReferences();
    ElMessage.success('Department added');
  });
}

async function handleCreateCategory() {
  await runAction(async () => {
    await createCategory(categoryForm);
    Object.assign(categoryForm, { name: '', description: '', anonymousAllowed: false, enabled: true, defaultDepartmentId: null, defaultSlaHours: 72, workflowTemplate: 'STANDARD' });
    await loadReferences();
    ElMessage.success('Category added');
  });
}

function setAnonymousPreference(value) {
  if (value && !selectedCategory.value?.anonymousAllowed) {
    caseForm.anonymous = false;
    ElMessage.warning('This category does not allow anonymous submissions.');
    return;
  }
  caseForm.anonymous = value;
}

async function handleGenerateReport() {
  await runAction(async () => {
    await generateWeeklyReport();
    reports.value = await getWeeklyReports();
    ElMessage.success('Weekly report generated');
  });
}

async function handleExportReports() {
  await runAction(async () => {
    const blob = await exportReports();
    const url = URL.createObjectURL(blob);
    window.open(url, '_blank');
    setTimeout(() => URL.revokeObjectURL(url), 5000);
  });
}

async function loadProfileData() {
  const profile = await getProfile();
  Object.assign(profileForm, profile);
}

async function handleSaveProfile() {
  await runAction(async () => {
    const profile = await updateProfile({ fullName: profileForm.fullName, email: profileForm.email });
    Object.assign(profileForm, profile);
    currentUser.value = { ...currentUser.value, fullName: profile.fullName, email: profile.email };
    ElMessage.success('Profile updated');
  });
}

async function handleChangePassword() {
  await runAction(async () => {
    if (passwordForm.newPassword !== passwordForm.confirmPassword) {
      throw new Error('New passwords do not match');
    }
    await changePassword({ oldPassword: passwordForm.oldPassword, newPassword: passwordForm.newPassword });
    Object.assign(passwordForm, { oldPassword: '', newPassword: '', confirmPassword: '' });
    ElMessage.success('Password changed');
  });
}

async function loadSettingsData() {
  if (currentUser.value?.role !== 'ADMIN') return;
  const settings = await getEmailSettings();
  Object.assign(emailSettings, settings);
  Object.assign(emailForm, {
    enabled: settings.enabled,
    host: settings.host || '',
    port: settings.port || 1025,
    username: settings.username || '',
    password: '',
    fromAddress: settings.fromAddress || '',
    startTls: settings.startTls
  });
}

async function handleSaveEmailSettings() {
  await runAction(async () => {
    const settings = await updateEmailSettings(emailForm);
    Object.assign(emailSettings, settings);
    emailForm.password = '';
    ElMessage.success('Email settings saved');
  });
}

async function handleSendTestEmail() {
  await runAction(async () => {
    testEmailResult.value = await sendTestEmail(testEmailTo.value);
    if (testEmailResult.value.sent) ElMessage.success('Test email sent');
    else ElMessage.error(testEmailResult.value.message);
  });
}

async function loadOverdueCases() {
  cases.value = await getOverdueCases();
}

async function openCase(row) {
  await runAction(async () => {
    if (currentUser.value.role === 'STUDENT') selectedCase.value = await getStudentCase(row.id);
    if (currentUser.value.role === 'OFFICER') selectedCase.value = await getOfficerCase(row.id);
    if (currentUser.value.role === 'DEPARTMENT_STAFF') selectedCase.value = await getDepartmentCase(row.id);
    if (currentUser.value.role === 'ADMIN') selectedCase.value = await getCaseDetail(row.id);
    assignForm.departmentId = selectedCase.value.assignedDepartmentId;
    assignForm.departmentIds = selectedCase.value.assignments?.length
      ? selectedCase.value.assignments.map((item) => item.departmentId)
      : (selectedCase.value.assignedDepartmentId ? [selectedCase.value.assignedDepartmentId] : []);
    privateNotes.value = currentUser.value.role === 'STUDENT' ? [] : await getPrivateNotes(selectedCase.value.id);
    detailOpen.value = true;
  });
}

async function handleStudentMessage() {
  await mutateSelected(() => sendStudentMessage(selectedCase.value.id, studentMessage.value), () => { studentMessage.value = ''; });
}

async function handleFollowUp() {
  await mutateSelected(() => requestFollowUp(selectedCase.value.id, followUpMessage.value), () => { followUpMessage.value = ''; });
}

async function handleReopen() {
  await mutateSelected(() => requestReopen(selectedCase.value.id, reopenMessage.value), () => { reopenMessage.value = ''; });
}

async function handleSatisfaction() {
  await mutateSelected(() => submitSatisfaction(selectedCase.value.id, satisfactionForm), () => { satisfactionForm.rating = 5; satisfactionForm.comment = ''; });
}

async function handleAssign() {
  await mutateSelected(() => assignCase(selectedCase.value.id, { departmentIds: assignForm.departmentIds, note: assignForm.note }), () => {});
}

async function handleRequestInfo() {
  await mutateSelected(() => requestCaseInfo(selectedCase.value.id, officerNote.value), () => { officerNote.value = ''; });
}

async function handleCloseCase() {
  await mutateSelected(() => closeCase(selectedCase.value.id, closeNote.value), () => { closeNote.value = ''; });
}

async function handleReminder() {
  await mutateSelected(() => createReminder(selectedCase.value.id, reminderNote.value || 'Reminder sent from overdue queue'), () => { reminderNote.value = ''; });
}

async function handleProgress() {
  await mutateSelected(() => updateCaseProgress(selectedCase.value.id, departmentNote.value), () => { departmentNote.value = ''; });
}

async function handleResolve() {
  await mutateSelected(() => resolveCase(selectedCase.value.id, resolveNote.value), () => { resolveNote.value = ''; });
}

async function handlePrivateNote() {
  await runAction(async () => {
    const note = await addPrivateNote(selectedCase.value.id, privateNoteForm);
    privateNotes.value = [note, ...privateNotes.value];
    Object.assign(privateNoteForm, { content: '', rootCause: '' });
    ElMessage.success('Internal note added');
  });
}

async function mutateSelected(action, cleanup) {
  await runAction(async () => {
    selectedCase.value = await action();
    cleanup();
    await refreshCases();
    await loadNotifications();
    ElMessage.success('Case updated');
  });
}

async function runAction(action) {
  loading.value = true;
  try {
    await action();
  } catch (error) {
    ElMessage.error(error.message);
  } finally {
    loading.value = false;
  }
}

function activateSession(response) {
  localStorage.setItem('scfs_token', response.token);
  currentUser.value = response.user;
  view.value = roleHome(response.user.role);
  refreshAll();
  startNotificationPolling();
}

async function refreshAll() {
  await Promise.all([refreshCases(), loadAdminData(), loadNotifications()]);
}

async function refreshCases() {
  if (currentUser.value?.role === 'STUDENT') cases.value = await getStudentCases();
  if (currentUser.value?.role === 'OFFICER') cases.value = await searchCases({});
  if (currentUser.value?.role === 'DEPARTMENT_STAFF') cases.value = await getDepartmentCases();
  if (currentUser.value?.role === 'ADMIN') cases.value = await searchCases({});
}

async function loadReferences() {
  const [departmentData, categoryData] = await Promise.all([getDepartments(), getCategories()]);
  departments.value = departmentData;
  categories.value = categoryData;
}

async function loadAdminData() {
  if (currentUser.value?.role === 'OFFICER') {
    reports.value = await getWeeklyReports();
    return;
  }
  if (currentUser.value?.role !== 'ADMIN') return;
  const [userData, reportData, auditData] = await Promise.all([getUsers(), getWeeklyReports(), getAuditLogs()]);
  users.value = userData;
  reports.value = reportData;
  auditLogs.value = auditData;
}

async function loadNotifications() {
  if (!currentUser.value) return;
  notifications.value = await getNotifications();
}

function startNotificationPolling() {
  stopNotificationPolling();
  notificationTimer = window.setInterval(() => {
    if (currentUser.value) loadNotifications();
  }, 30000);
}

function stopNotificationPolling() {
  if (!notificationTimer) return;
  window.clearInterval(notificationTimer);
  notificationTimer = null;
}

async function handleNotificationClick(item) {
  await runAction(async () => {
    if (!item.readFlag) {
      const updated = await markNotificationRead(item.id);
      notifications.value = notifications.value.map((candidate) => candidate.id === updated.id ? updated : candidate);
    }
    if (!canOpenNotificationCase(item)) return;
    selectedCase.value = await getCaseDetail(item.caseId);
    assignForm.departmentId = selectedCase.value.assignedDepartmentId;
    assignForm.departmentIds = selectedCase.value.assignments?.length
      ? selectedCase.value.assignments.map((assignment) => assignment.departmentId)
      : (selectedCase.value.assignedDepartmentId ? [selectedCase.value.assignedDepartmentId] : []);
    privateNotes.value = currentUser.value.role === 'STUDENT' ? [] : await getPrivateNotes(selectedCase.value.id);
    detailOpen.value = true;
  });
}

function logout() {
  stopNotificationPolling();
  localStorage.removeItem('scfs_token');
  currentUser.value = null;
  users.value = [];
  cases.value = [];
  reports.value = [];
  notifications.value = [];
  selectedCase.value = null;
  detailOpen.value = false;
  view.value = 'login';
}

function dateOnly(value) {
  return value ? new Date(value).toLocaleDateString() : 'N/A';
}

function dateTime(value) {
  return value ? new Date(value).toLocaleString() : 'N/A';
}
</script>

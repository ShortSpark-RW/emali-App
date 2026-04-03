# Plan: Improve IMPLEMENTATION_ROADMAP.md

## Current State Analysis
- Document last updated: April 1, 2025 (over a year old)
- Current branch: `ft/auth-screen-designs` with many auth files modified
- Current date: April 2, 2026
- Roadmap shows mixed completion metrics that may not reflect reality

## Proposed Improvements

### 1. **Update Document Metadata & Dates**
- Change "Last Updated" to current date
- Update project timeline from 12 weeks to reflect actual progress
- Add "Current Sprint" section

### 2. **Restructure for Actionability**
- Add clear Table of Contents with anchor links
- Standardize status indicators:
  - `🟢 Done` - Feature complete and tested
  - `🟡 In Progress` - Actively being worked on
  - `🔴 Not Started` - No work yet
  - `🟠 Blocked` - Waiting on dependencies
  - `⚪ On Hold` - Temporarily paused
- Add "Definition of Done" section per epic

### 3. **Add Missing Critical Information**
- **Ownership**: Assign primary owner for each epic (even if TBD)
- **Dependencies**: Explicit task dependencies (what blocks what)
- **Risk Register**: Technical risks with mitigation plans
- **Performance Benchmarks**: Quantitative KPIs for each module
- **Security Requirements**: Auth, data encryption, API security checklists
- **Accessibility Standards**: WCAG compliance targets

### 4. **Improve Sprint Task Breakdowns**
Current issues:
- "Build HomeScreen" is too vague
- No acceptance criteria
- No subtask breakdown

Better format:
```
**Sprint 1.1: Property Listing & Browse**
- [ ] HomeScreen UI implementation
  - [ ] Create PropertyCard composable with image, title, price, location
  - [ ] Implement LazyVerticalGrid for responsive layout
  - [ ] Add shimmer loading placeholders
  - [ ] Implement pull-to-refresh with SwipeRefresh
- [ ] API integration
  - [ ] Wire up PropertySDK.getAllProperties(page, limit)
  - [ ] Implement 5-minute caching with SQLDelight
  - [ ] Add error handling with RequestState.Error
  - [ ] Add retry logic with exponential backoff
- [ ] Infinite scroll pagination
  - [ ] Detect scroll to end with derivedStateOf
  - [ ] Load next page automatically
  - [ ] Show loading indicator at bottom
  - [ ] Handle empty state (no properties)
- **Acceptance Criteria:**
  - User can scroll through 50+ properties without lag
  - Pull-to-refresh fetches latest data
  - Network errors show retry button
  - Empty state shows "No properties found"
```

### 5. **Add Technical Implementation Details**
For each KMM-specific challenge:
- **Image Picker**: Library choice (`compose-multiplatform-image-picker`), permission handling, compression
- **Video Playback**: ExoPlayer (Android) vs AVPlayer (iOS), caching strategy
- **Maps**: `compose-maps` setup, API keys, marker clustering
- **Camera**: Platform-specific implementations, file handling
- **Payments**: Expect/actual pattern for SDKs, webhook handling
- **Push Notifications**: FCM vs APNs integration approach

### 6. **Create API Contract Details**
Instead of just endpoint names, include:
```
POST /api/v1/properties
**Request:**
{
  "title": "string",
  "description": "string",
  "type": "HOUSE|APARTMENT|LAND",
  "saleType": "SALE|RENT",
  "price": number,
  "location": {
    "address": "string",
    "lat": number,
    "lng": number,
    "placeId": "string"
  },
  ...
}

**Response (201):**
{
  "id": "uuid",
  "title": "string",
  ...
}
```

### 7. **Add Testing Strategy Per Feature**
Move generic testing section to feature-specific:
- Unit tests: ViewModel, Repository, UseCase
- Integration tests: API, Database
- UI tests: Compose Test, Navigation
- E2E tests: Critical user flows
- Test coverage targets: 80%+

### 8. **Include CI/CD & DevOps**
- Build pipeline stages ( Android, iOS, backend )
- Automated testing in CI
- Code quality gates (ktlint, detekt, SonarQube)
- Release automation
- Monitoring setup (Crashlytics, Sentry, LogRocket)
- Feature flags for gradual rollouts

### 9. **Add Rollback & Emergency Procedures**
- Rollback checklist for each release
- Database migration rollback scripts
- Emergency contact procedures
- Incident response playbook

### 10. **Create Progress Tracking Dashboard**
Add a summary table at top showing:
| Epic | Current Status | Sprint | Owner | Blockers | Next Milestone |
|------|---------------|--------|-------|----------|----------------|

### 11. **Update Completion Percentages Based on Reality**
Review git status to determine actual completion:
- Auth screens: Many files modified → likely 80-90%
- Home/Property: `MainScreen1.kt` modified but placeholder → ~30%
- Map/Tours/Payments: Placeholders only → 0-5%
- Backend: Assume APIs are ready → 90%+

### 12. **Add Definition of Done (DoD) Section**
Each epic must have:
- ✅ Code complete and peer-reviewed
- ✅ Unit tests ≥ 80% coverage
- ✅ UI tests for critical flows
- ✅ Backend API tested with Postman/Insomnia
- ✅ Documentation updated (API docs, ADRs)
- ✅ Staging deployed and QA verified
- ✅ No P1/P2 bugs open
- ✅ Performance benchmarks met

### 13. **Add Accessibility & Internationalization**
- WCAG 2.1 AA compliance checklist
- Font scaling support (-3 to +3)
- Screen reader testing
- RTL layout support
- Translation ready (strings.xml/localizable)

### 14. **Add Performance Benchmarks**
- **API Response Times**: <200ms for 95th percentile
- **Load Times**: <2s to interactive on 3G
- **Memory Usage**: <150MB on average
- **APK Size**: <30MB (without resources)
- **Crash Rate**: <0.1% sessions
- **Frame Rate**: 60fps with <16ms frame time

### 15. **Sync with Jira (if available)**
- Pull actual epic/ticket status from Jira
- Link to specific tickets instead of generic tasks
- Include Jira issue keys in roadmap (e.g., SCRUM-123)

### 16. **Add Resources & Reference Links Section**
- Live backend API URL (dev/staging/prod)
- Postman collection link
- Figma file with specific node IDs for each screen
- Architecture decision records (ADR) index
- Team contact matrix (backend, mobile, design, PM)

### 17. **Better Figma Integration**
Instead of just a link, create a mapping table:
| Screen | Figma Node | Status | Implemented By |
|--------|------------|--------|----------------|
| Signin | 1:234 | ✅ Done | @igor |
| Home | 2:567 | 🟡 In Progress | @igor |
| Property Detail | 3:890 | 🔴 Not Started | - |

### 18. **Add Migration Strategy**
If replacing old screens (like MainScreen → MainScreen1):
- Document what's being replaced
- Keep old code until new is verified
- Deprecation timeline
- Rollback plan

### 19. **Include Unknown Unknowns Section**
Acknowledge uncertainty:
- "Video feed architecture TBD based on backend capabilities"
- "Payment provider selection pending"
- "iOS build issues may require additional time"

### 20. **Create Separate File for Sprint Details**
Split into:
- `IMPLEMENTATION_ROADMAP.md` - High-level overview (current)
- `SPRINT_PLAN.md` - Detailed 2-week sprint plans
- `API_CONTRACTS.md` - Complete API spec
- `ARCHITECTURE_DECISIONS.md` - ADRs
- `CHECKLISTS.md` - DoD checklists

---

## Implementation Order

I'll update the document in this sequence:

1. **First Pass** - Update dates, fix glaring inconsistencies, add TOC
2. **Second Pass** - Restructure sprints with better task breakdowns
3. **Third Pass** - Add technical details, API contracts, testing
4. **Fourth Pass** - Add project management sections (ownership, risks, DoD)
5. **Fifth Pass** - Polish formatting, links, cross-references

---

## Specific Changes to Make

### Remove/Archive:
- Old completion percentages that don't match reality
- Vague statements like "Build HomeScreen"
- Duplicate information

### Add:
- Current status based on branch
- Clear task breakdowns with subtasks
- Acceptance criteria for each feature
- Owner assignments
- Dependency arrows (A → B)
- Risk items with owners and mitigation
- Timeline correction (it's 2026, not 2025)
- Clear "Definition of Done" per epic

---

## Estimated Effort
- Time to rewrite: 2-3 hours
- Review time: 30 minutes with user
- Value: High - provides clear execution plan, reduces ambiguity

---

## Questions for User

Before I proceed, I need:

1. **Backend Status**: Which API endpoints are actually working in dev/staging?
2. **Jira Access**: Can I query your Jira project? If yes, what's the project key? (From roadmap: SCRUM)
3. **Current Focus**: What should be the next immediate priority? (From git: auth screens modified, maybe auth is the current focus?)
4. **Team**: Are you solo or with a team? (affects ownership section)
5. **Timeline**: What's the realistic launch date now? (Original was 12 weeks from April 2025, but it's now April 2026 - major delay or was roadmap aspirational?)

---

Once you answer these, I'll create the improved roadmap.

**Would you like me to proceed with the rewrite based on the plan above?** I can make reasonable assumptions if you don't have answers to all questions.

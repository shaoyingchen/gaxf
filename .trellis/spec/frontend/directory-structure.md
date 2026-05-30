# Directory Structure

> How frontend code is organized in this project.

---

## Overview

Vue 3.5 + TypeScript 5.6 + Vite 6.4 SPA based on RuoYi-Vue3 v3.9.1. Located in `gaxf-ui/`.

---

## Directory Layout

```
gaxf-ui/
├── index.html                 # HTML entry
├── package.json               # Dependencies & scripts
├── vite.config.ts             # Vite build config
├── tailwind.config.js         # Tailwind CSS config
├── postcss.config.cjs         # PostCSS config
├── tsconfig.json              # TypeScript config
├── auto-imports.d.ts          # Auto-import type declarations
├── public/                    # Static assets (favicon, etc.)
├── html/                      # Static HTML pages
├── vite/                      # Vite plugin configs
│   └── plugins/
└── src/
    ├── main.ts                # App entry point
    ├── App.vue                # Root component
    ├── permission.ts          # Router guards (auth, lock screen)
    ├── settings.ts            # UI settings (theme, sidebar, tagsView)
    ├── env.d.ts               # Env type declarations
    ├── api/                   # API request modules (one per domain)
    │   ├── login.ts
    │   ├── menu.ts
    │   ├── spdc/              # Business: camera, dict, pga, alert
    │   ├── system/            # System: user, role, dept, menu, dict, config, notice, post
    │   ├── monitor/           # Monitor: cache, job, logininfor, online, operlog, server
    │   └── tool/              # Tools: code gen
    ├── assets/                # Images, styles, icons
    ├── components/            # Shared reusable components
    │   ├── Breadcrumb/
    │   ├── DictTag/
    │   ├── Editor/
    │   ├── FileUpload/
    │   ├── ImageUpload/
    │   ├── Pagination/
    │   ├── RightToolbar/
    │   ├── SvgIcon/
    │   └── ...
    ├── directive/             # Custom Vue directives
    │   ├── common/            # Common directives
    │   └── permission/        # v-hasPermi, v-hasRole
    ├── layout/                # App shell layout
    ├── plugins/               # Plugin modules
    │   ├── auth.ts            # Permission check helpers (hasPermi, hasRole)
    │   ├── cache.ts           # Session/localStorage wrapper
    │   ├── download.ts        # File download helper
    │   ├── modal.ts           # Modal helpers
    │   └── tab.ts             # Tab helpers
    ├── router/                # Vue Router config
    ├── store/                 # Pinia stores
    │   ├── index.ts           # createPinia()
    │   └── modules/           # app, dict, lock, permission, settings, tagsView, user
    ├── types/                 # TypeScript type definitions
    │   ├── index.ts           # Re-exports
    │   ├── api/               # API types per domain (common, login, spdc/*, system/*)
    │   ├── components.d.ts    # Component type declarations
    │   └── global.d.ts        # Global type augmentations
    ├── utils/                 # Utility functions
    │   ├── request.ts         # Axios instance + interceptors
    │   ├── auth.ts            # Token get/set/remove (via js-cookie)
    │   ├── dict.ts            # Dict data cache
    │   ├── validate.ts        # Validation helpers
    │   ├── ruoyi.ts           # RuoYi-specific utils (tansParams, blobValidate)
    │   ├── errorCode.ts       # Error code mapping
    │   ├── jsencrypt.ts       # RSA encryption
    │   └── ...
    └── views/                 # Page components (one dir per module)
        ├── login.vue
        ├── register.vue
        ├── lock.vue
        ├── redirect/
        ├── error/
        ├── spdc/              # Business pages
        ├── system/            # System management pages
        ├── monitor/           # Monitor pages
        └── tool/              # Tool pages
```

---

## Module Organization

New features follow this structure:

```
src/
├── api/{module}/
│   └── {entity}.ts           # API calls for the entity
├── types/api/{module}/
│   └── {entity}.ts           # TypeScript interfaces
├── views/{module}/
│   └── {entity}/
│       └── index.vue         # Main page component
└── components/               # Shared components only (not module-specific)
```

---

## Naming Conventions

| Item | Convention | Example |
|------|-----------|---------|
| API file | camelCase | `camera.ts`, `alert.ts` |
| API function | verb + Entity | `listCamera`, `getCamera`, `addCamera`, `updateCamera`, `delCamera` |
| Type file | matches API file | `camera.ts` in `types/api/spdc/` |
| Type interface | PascalCase | `DcCamera`, `CameraQueryParams` |
| View directory | kebab-case or match module | `spdc/`, `system/` |
| Store module | camelCase | `user.ts`, `permission.ts` |
| Component directory | PascalCase | `FileUpload/`, `DictTag/` |
| Utility file | camelCase | `request.ts`, `validate.ts` |

---

## Examples

The `spdc` module is the reference business module:
- `src/api/spdc/camera.ts` — API calls
- `src/types/api/spdc/camera.ts` — TypeScript interfaces
- `src/views/spdc/` — Page components

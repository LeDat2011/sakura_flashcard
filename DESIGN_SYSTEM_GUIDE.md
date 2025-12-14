# 🎨 Sakura Flashcard - Design System Guide

## Overview
Design system được lấy từ web template React/TypeScript, áp dụng vào Kotlin Jetpack Compose.

---

## 🎯 Color Palette

### Primary Colors (Sky Blue)
```kotlin
AppColors.PrimaryLight          // 0x0ea5e9 - Main action color
AppColors.PrimaryLighter        // 0x7dd3fc - Lighter shade for hover
AppColors.PrimaryLightest       // 0xe0f2fe - Lightest for backgrounds
```

### Secondary Colors (Rose/Pink)
```kotlin
AppColors.SecondaryLight        // 0xfb7185 - Secondary action
AppColors.SecondaryLighter      // 0xfecdd3 - Secondary hover
AppColors.SecondaryLightest     // 0xffe4e6 - Secondary background
```

### Status Colors
```kotlin
AppColors.SuccessLight          // 0x10b981 - Success state
AppColors.WarningLight          // 0xf97316 - Warning/attention
AppColors.DangerLight           // 0xef4444 - Error/danger
```

### Text Colors
```kotlin
AppColors.TextPrimary           // 0x1e293b - Main text (headings, body)
AppColors.TextSecondary         // 0x64748b - Secondary text
AppColors.TextTertiary          // 0x94a3b8 - Disabled/muted text
```

### Surface Colors
```kotlin
AppColors.SurfaceLight          // 0xffffff - Cards, panels
AppColors.SurfaceMedium         // 0xf8fafc - Page background
AppColors.SurfaceBorder         // 0xe2e8f0 - Border color for cards
```

---

## 🧩 Component Patterns

### 1. PRIMARY BUTTON
Used for main actions (Check, Submit, Next, etc.)

```kotlin
Button(
    onClick = { /* action */ },
    colors = ButtonDefaults.buttonColors(
        containerColor = AppColors.PrimaryLight
    ),
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
) {
    Text(
        "Continue",
        style = AppTypography.TitleMedium,
        color = Color.White
    )
}
```

### 2. SECONDARY BUTTON
Used for alternative actions

```kotlin
Button(
    onClick = { /* action */ },
    colors = ButtonDefaults.buttonColors(
        containerColor = AppColors.SurfaceLight
    ),
    border = BorderStroke(1.dp, AppColors.SurfaceBorder),
    shape = RoundedCornerShape(16.dp),
    modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
) {
    Text(
        "Cancel",
        style = AppTypography.TitleMedium,
        color = AppColors.TextPrimary
    )
}
```

### 3. ICON BUTTON (Active/Inactive)
```kotlin
Button(
    onClick = { /* action */ },
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = if (isActive) AppColors.PrimaryLightest 
                         else Color.Transparent
    ),
    modifier = Modifier
        .size(48.dp)
        .padding(8.dp)
) {
    Icon(
        imageVector = icon,
        contentDescription = label,
        tint = if (isActive) AppColors.PrimaryLight 
               else AppColors.TextSecondary,
        modifier = Modifier.size(24.dp)
    )
}
```

### 4. BADGE/TAG
```kotlin
Surface(
    color = AppColors.PrimaryLightest,
    shape = RoundedCornerShape(8.dp)
) {
    Text(
        "N5",
        style = AppTypography.LabelSmall,
        color = AppColors.PrimaryLight,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
```

### 5. CARD
```kotlin
Card(
    modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() },
    colors = CardDefaults.cardColors(
        containerColor = AppColors.SurfaceLight
    ),
    border = BorderStroke(1.dp, AppColors.SurfaceBorder),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    shape = RoundedCornerShape(20.dp) // rounded-3xl
) {
    // Content inside
}
```

### 6. GRADIENT CARD (Welcome Banner)
```kotlin
val gradientBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xf0f9ff),  // sky-50
        Color(0xeff6ff)   // blue-50
    )
)

Card(
    modifier = Modifier
        .background(gradientBrush)
        .clip(RoundedCornerShape(24.dp)),
    colors = CardDefaults.cardColors(
        containerColor = Color.Transparent
    ),
    border = BorderStroke(1.dp, AppColors.PrimaryLightest),
    shape = RoundedCornerShape(24.dp)
) {
    // Content
}
```

### 7. BOTTOM NAV ITEM (Active State)
```kotlin
Button(
    onClick = { setScreen(item.id) },
    shape = RoundedCornerShape(12.dp),
    colors = ButtonDefaults.buttonColors(
        containerColor = if (isActive) AppColors.PrimaryLightest 
                         else Color.Transparent
    ),
    modifier = Modifier
        .size(56.dp)
        .padding(8.dp)
) {
    Icon(
        imageVector = item.icon,
        contentDescription = item.label,
        tint = if (isActive) AppColors.PrimaryLight 
               else AppColors.TextSecondary,
        modifier = Modifier.size(24.dp)
    )
}
```

### 8. STAT BADGE (Profile/Dashboard)
```kotlin
Surface(
    color = bgColor, // e.g., AppColors.PrimaryLightest
    shape = RoundedCornerShape(16.dp)
) {
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "12",
            style = AppTypography.TitleMedium,
            color = AppColors.TextPrimary
        )
        Text(
            "Days",
            style = AppTypography.LabelSmall,
            color = textColor // e.g., AppColors.WarningLight
        )
    }
}
```

---

## 📐 Spacing & Sizing

### Standard Spacing Values (in dp)
```
4dp   = spacing-1
8dp   = spacing-2
12dp  = spacing-3
16dp  = spacing-4
20dp  = spacing-5
24dp  = spacing-6
32dp  = spacing-8
48dp  = spacing-12
64dp  = spacing-16
```

### Border Radius
```
8dp   = rounded-lg   (small elements)
12dp  = rounded-xl   (buttons, icons)
16dp  = rounded-2xl  (cards, level badges)
20dp  = rounded-3xl  (main cards, banners)
24dp  = rounded-full (perfect circles)
```

### Standard Button Heights
```
40dp  = Small button (text-only)
48dp  = Medium button (primary action)
56dp  = Large button (full-width)
44dp  = Icon button (round)
```

---

## 🎨 Typography

### Headlines
- **HeadlineLarge**: 28sp, Bold (page titles)
- **HeadlineSmall**: 20sp, SemiBold (section titles)

### Titles
- **TitleLarge**: 18sp, Bold (card headers)
- **TitleMedium**: 16sp, SemiBold (subsections)

### Body
- **BodyLarge**: 16sp, Normal (main content)
- **BodyMedium**: 14sp, Normal (secondary content)
- **BodySmall**: 12sp, Normal (tertiary content)

### Labels
- **LabelLarge**: 14sp, SemiBold (button text)
- **LabelSmall**: 11sp, SemiBold (badges, captions)

---

## 🎬 Animations & Interactions

### Button Feedback
```kotlin
.active:scale-95        // Pressed state
Modifier
    .clickable {
        // action
    }
    .indication(ripple()) // Default ripple effect
```

### Tab/Nav Active State
```kotlin
Modifier
    .animateContentSize()  // Smooth size change
    .transition(
        scaleIn() + fadeIn()
    )
```

### Hover States
- Button: Change color (lighter shade)
- Card: Scale 1.02, change background to slate-50
- Icon: Color change to primary

---

## 📝 Web-to-Mobile Color Mapping

| Web Class | Kotlin Color | Usage |
|-----------|--------------|-------|
| `bg-sky-500` | `AppColors.PrimaryLight` | Primary buttons, active states |
| `bg-sky-100` | `AppColors.PrimaryLightest` | Button hover, active backgrounds |
| `bg-white` | `AppColors.SurfaceLight` | Cards, panels |
| `bg-slate-50` | `AppColors.SurfaceMedium` | Page background |
| `text-slate-800` | `AppColors.TextPrimary` | Main text |
| `text-slate-500` | `AppColors.TextSecondary` | Secondary text |
| `border-slate-100` | `AppColors.SurfaceBorder` | Card borders |

---

## 🔄 Category Colors (Topics/Badges)

```kotlin
// Different categories for vocabulary topics
AppColors.CategoryOrange   // 0xfb923c - Greetings, Common
AppColors.CategoryGreen    // 0x4ade80 - Food, Positive
AppColors.CategoryPurple   // 0xc084fc - Business, Formal
AppColors.CategoryBlue     // 0x38bdf8 - Travel, Places
AppColors.CategoryPink     // 0xf472b6 - Family, Emotions
AppColors.CategoryYellow   // 0xfbbf24 - Numbers, Time
```

---

## ✅ Implementation Checklist

When implementing a new screen:
- [ ] Import `AppColors` and `AppTypography`
- [ ] Use `AppColors.*` for all colors
- [ ] Use `AppTypography.*` for all text styles
- [ ] Apply appropriate spacing (4dp, 8dp, 12dp, 16dp, 24dp)
- [ ] Use `RoundedCornerShape(16.dp)` for buttons/small cards
- [ ] Use `RoundedCornerShape(20.dp)` for main cards
- [ ] Add `BorderStroke(1.dp, AppColors.SurfaceBorder)` to cards
- [ ] Use `CardDefaults.cardElevation(defaultElevation = 1.dp)` for subtle shadows
- [ ] Implement active/inactive states with color changes
- [ ] Test dark mode compatibility (if implemented)

---

## 📚 Files Reference

- **DesignSystem.kt** - Color definitions, typography styles
- **HomeScreen.kt** - Example of updated implementation
- **Web templates** (React) - Reference for design patterns

---

Last updated: Dec 14, 2025

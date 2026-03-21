# Supabase + Google OAuth Setup

This guide explains how to connect Google sign-in with Supabase and use it with this backend.

## 1. Supabase Dashboard Setup

1. Go to [Supabase Dashboard](https://supabase.com/dashboard) → your project
2. **Enable Google OAuth**
   - Authentication → Providers → Google → Enable
   - Add your **Google Client ID** and **Client Secret** from [Google Cloud Console](https://console.cloud.google.com/apis/credentials)
   - Add redirect URI: `https://<your-project-ref>.supabase.co/auth/v1/callback`

3. **Get JWT Secret**
   - Settings → API → **JWT Secret** (copy this value)

## 2. Google Cloud Console Setup

1. Create OAuth credentials (Web application)
2. Add authorized redirect URI: `https://<project-ref>.supabase.co/auth/v1/callback`
3. Add authorized JavaScript origins: `http://localhost:5173` (dev), your production URL
4. Configure OAuth consent screen with scopes: `openid`, `userinfo.email`, `userinfo.profile`

## 3. Backend Configuration

Add to `application.properties` or set environment variable:

```properties
# From Supabase: Settings → API → JWT Secret
supabase.jwt-secret=your-jwt-secret-here
```

Or use environment variable (recommended for production):

```bash
SUPABASE_JWT_SECRET=your-jwt-secret-here
```

## 4. Frontend Integration

Example with Supabase JS client:

```javascript
import { createClient } from '@supabase/supabase-js'

const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY)

// Sign in with Google
const { data, error } = await supabase.auth.signInWithOAuth({ provider: 'google' })
// User is redirected to Google, then back to your app

// After redirect, get session
const { data: { session } } = await supabase.auth.getSession()

if (session?.access_token) {
  // Exchange for salon JWT
  const res = await fetch('http://localhost:8081/api/auth/supabase/exchange', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      accessToken: session.access_token,
      salonName: 'Your Salon Name',  // required for new customers
      role: 'ROLE_CUSTOMER'          // or 'ROLE_ADMIN'
    })
  })
  const { token } = await res.json()
  // Use token for API calls (Authorization: Bearer <token>)
}
```

## 5. API Endpoint

| Endpoint | Method | Body | Description |
|----------|--------|------|-------------|
| `/api/auth/supabase/exchange` | POST | `{ accessToken, salonName?, role? }` | Exchange Supabase JWT for salon JWT |

- **accessToken**: Required. From `session.access_token` after Google sign-in
- **salonName**: Required for **new** users (to associate with a salon)
- **role**: `ROLE_CUSTOMER` (default) or `ROLE_ADMIN`

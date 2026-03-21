# Stripe Payments – Low-Cost Setup

**Cost: 2.9% + $0.30 per transaction. No monthly fees.**

## 1. Create Stripe Account
- Sign up at [stripe.com](https://stripe.com) (free)
- Get API keys from Dashboard → Developers → API keys

## 2. Create Products & Prices
1. Dashboard → Products → Add product
2. Create 3 products: Basic ($49/mo), Pro ($99/mo), Enterprise ($199/mo)
3. Add recurring price (monthly) for each
4. Copy the **Price ID** (starts with `price_`)

## 3. Configure Backend
Add to `application.properties` or environment variables:

```properties
stripe.secret-key=sk_test_...   # or sk_live_... for production
stripe.price-id.basic=price_xxx
stripe.price-id.pro=price_xxx
stripe.price-id.enterprise=price_xxx
```

## 4. API Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/payments/checkout` | POST | Admin | Create checkout, returns Stripe URL |
| `/api/payments/confirm?session_id=xxx` | POST | Admin | Confirm after redirect, activate subscription |

## 5. Frontend Flow
1. Admin selects plan → POST `/api/payments/checkout` with `{ "planId": 1 }`
2. Redirect user to returned `checkoutUrl`
3. User pays on Stripe (PCI compliant)
4. Stripe redirects to success URL with `?session_id=xxx`
5. Frontend calls POST `/api/payments/confirm?session_id=xxx` to activate

## 6. Revenue Example
- 50 salons × $99/mo Pro plan = **$4,950/month**
- Stripe fee: ~$150 (2.9%+$0.30) → **~$4,800 net**

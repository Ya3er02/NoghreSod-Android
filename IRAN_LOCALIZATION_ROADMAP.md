# 🇮🇷 راهنمای پیاده‌سازی بازار ایران

**رودماپ اجرای کامل NoghreSod برای بازار ایرانیان**

---

## فاز 1️⃣: رابط کاربری فارسی (۱-۲ هفته)

### وظایف اعمال

- [ ] **فعال‌سازی RTL**
  - [ ] `android:supportsRtl="true"` در AndroidManifest.xml
  - [ ] تمام `Text` داخل Compose: `textAlign = TextAlign.End`
  - [ ] `Row` لاياوت: `horizontalArrangement = Arrangement.SpaceBetween`
  - [ ] حزف `LTR` margins و padding
  - [ ] تست: برنامه را به فارسی تبدیل کنید
  - **زمان: 2-3 روز**
  - **تماس: 15 فایل**

- [ ] **فونت‌های فارسی**
  - [ ] Vazir Regular و Bold را download کنید
  - [ ] فایل‌ها را `res/font/` منتقل کنید
  - [ ] `FontFamily` object بسازید
  - [ ] Material Theme را update کنید
  - [ ] تمام Screens ارزیابی کنید
  - **زمان: 1-2 روز**
  - **تماس: 20+ فایل**

- [ ] **رنگ‌های ایرانی**
  - [ ] `PersianColors` object بسازید
  - [ ] Material Color Scheme را update کنید
  - [ ] تمام Buttons رنگبروبی کنید
  - **زمان: 1 روز**

**Deliverables Phase 1:**
- ✅ RTL layout 100%
- ✅ Persian fonts loaded
- ✅ Persian color scheme
- ✅ All UI properly aligned

---

## فاز 2️⃣: تقویم و اعداد فارسی (۱-۲ هفته)

### وظایف اعمال

- [ ] **تقویم جلالی**
  - [ ] `PersianDateConverter` object بسازید
  - [ ] Gregorian → Persian تبدیل function
  - [ ] Persian month نام‌ها
  - [ ] Day of week names
  - [ ] Unit tests برای تبدیلها
  - [ ] Order date نمایش از Persian date
  - **زمان: 2-3 روز**

- [ ] **اعداد فارسی**
  - [ ] `toFarsiNumbers()` function
  - [ ] اعداد را ۰-۹ به تبدیل کنید
  - [ ] قیمت‌ها با Persian numerals
  - [ ] Phone numbers با Persian digits
  - [ ] تاریخ‌ها با Persian numerals
  - **زمان: 1-2 روز**

- [ ] **بهبود ماه‌ها**
  - [ ] `formatPrice()` با separator
  - [ ] `formatPhoneNumber()` برای 0910 123 4567
  - [ ] `formatCardNumber()` برای masked display
  - **زمان: 1 روز**

**Deliverables Phase 2:**
- ✅ Jalali dates everywhere
- ✅ Persian number formatting
- ✅ Proper date display in UI

---

## فاز 3️⃣: درگاه‌های پرداخت (2-3 هفته)

### وظایف اعمال

- [ ] **زرین‌پال (پیشگفرتر)**
  - [ ] Zarinpal API sandbox با test credentials
  - [ ] `ZarinpalPaymentService` ابقاء راک (از قبل در Session 1)
  - [ ] Request payment flow
  - [ ] Callback handling
  - [ ] Verification after return
  - [ ] Unit tests
  - **زمان: 2-3 روز** (Already done)

- [ ] **آی‌دی‌پی**
  - [ ] IDPay API research
  - [ ] `IDPayPaymentService` implementation
  - [ ] Request/Verify flows
  - [ ] Error handling
  - [ ] Unit tests
  - [ ] Sandbox testing
  - **زمان: 2-3 روز**

- [ ] **نکست‌پی**
  - [ ] NextPay API research
  - [ ] `NextPayPaymentService` implementation
  - [ ] Request/Verify flows
  - [ ] Error handling
  - [ ] Unit tests
  - **زمان: 2-3 روز**

- [ ] **Payment Gateway Selection UI**
  - [ ] User interface to choose gateway
  - [ ] Show gateway logos/names
  - [ ] Remember last used gateway
  - [ ] Handle errors gracefully
  - **زمان: 1-2 روز**

**Deliverables Phase 3:**
- ✅ Zarinpal integration (already done)
- ✅ IDPay integration
- ✅ NextPay integration
- ✅ Payment gateway selection UI

---

## فاز 4️⃣: ویژگی‌های منطقه‌ای (2-3 هفته)

### وظایف اعمال

- [ ] **استان‌های ایران**
  - [ ] List of 31 provinces
  - [ ] Shipping cost calculation per province
  - [ ] Delivery time estimate per province
  - [ ] Auto-populate when address selected
  - **زمان: 2-3 روز**

- [ ] **ارسال بومی**
  - [ ] دنا (Post office)
  - [ ] منتقل الباد (حمل و نقل)
  - [ ] جور پست
  - [ ] لوازم مربوطه
  - [ ] زرباخر (با tracking)
  - **زمان: 2-3 روز**

- [ ] **محاسبه هزینه ارسال**
  - [ ] Base cost per province
  - [ ] Weight-based surcharge
  - [ ] Priority shipping options
  - [ ] Free shipping thresholds
  - [ ] Real-time calculation
  - **زمان: 1-2 روز**

- [ ] **بیمه‌ي کالا**
  - [ ] Optional insurance for shipments
  - [ ] Insurance cost based on value
  - [ ] Claim process
  - [ ] Documentation
  - **زمان: 2-3 روز**

**Deliverables Phase 4:**
- ✅ 31 Iranian provinces
- ✅ Multiple shipping methods
- ✅ Smart shipping cost calculation
- ✅ Optional insurance

---

## فاز 5️⃣: مقررات راهی و قانونی (1-2 هفته)

### وظایف اعمال

- [ ] **مالیات ایرانی**
  - [ ] Tax rate: 9% (VAT)
  - [ ] Applied to (basePrice + shipping)
  - [ ] Displayed in order summary
  - [ ] Final total calculation
  - [ ] Compliance with Iranian law
  - **زمان: 1 روز**

- [ ] **قیمت‌گذاری نقره و طلا**
  - [ ] Integration with Iran commodity prices API
  - [ ] Real-time silver prices (per gram)
  - [ ] Gold prices (optional for future)
  - [ ] Update frequency (daily/hourly)
  - [ ] Fallback prices
  - [ ] Display price as (/g)
  - **زمان: 2-3 روز**

- [ ] **رگولاسیون تحریمی**
  - [ ] Approved payment gateways only
  - [ ] No PayPal, Stripe, etc.
  - [ ] Check against sanctioned list
  - [ ] Block restricted services
  - **زمان: 1 روز**

- [ ] **نظارت قانونی**
  - [ ] Terms of Service (Persian)
  - [ ] Privacy Policy (Persian)
  - [ ] Money-back guarantee
  - [ ] Return policy
  - [ ] Warranty information
  - **زمان: 1 روز**

**Deliverables Phase 5:**
- ✅ Tax compliance
- ✅ Real-time pricing
- ✅ Regulation compliance
- ✅ Legal documentation

---

## پروزه کلي

### Timeline

```
Phase 1 (RTL+Fonts+Colors):      1-2 weeks     |▒▒▒▒|
                                              |▒▒|

Phase 2 (Dates+Numbers):         1-2 weeks           |▒▒▒▒|
                                                   |▒▒|

Phase 3 (Payment Gateways):      2-3 weeks                |▒▒▒▒▒▒|
                                                        |▒▒▒|

Phase 4 (Regional Features):     2-3 weeks                    |▒▒▒▒▒▒|
                                                          |▒▒▒|

Phase 5 (Regulations):           1-2 weeks                         |▒▒▒▒|
                                                                 |▒▒|

TOTAL EFFORT:                    7-12 weeks

With parallel work:              4-6 weeks (recommended)
```

### Team Recommendation

**Best approach: Parallel sprints**

```
Sprint 1 (Weeks 1-2):
  - Team A: Phase 1 (RTL, Fonts, Colors)
  - Team B: Phase 2 (Dates, Numbers)
  - Team C: Phase 3 prep (Payment research)

Sprint 2 (Weeks 3-4):
  - Team A: Phase 3 (Payment implementation)
  - Team B: Phase 4 (Regional features)
  - Team C: Phase 5 (Regulations)

Final Sprint (Week 5-6):
  - Integrate all phases
  - End-to-end testing
  - Performance optimization
  - Launch preparation
```

---

## قابلیت‌های عایده‌ای

### Phase 1 Checklist

```
☑ RTL layout 100% working
☑ Vazir font loaded and applied
☑ Persian color scheme
☑ All buttons, texts properly aligned
☑ Testing on RTL locale (fa-IR)
☑ Testing on both LTR and RTL modes
```

### Phase 2 Checklist

```
☑ Jalali dates everywhere
☑ Persian numerals in all numbers
☑ Unit tests for date conversion
☑ Proper formatting (e.g., ۱۴۰۳/۱۰/۰۶)
☑ Performance tested
```

### Phase 3 Checklist

```
☑ Zarinpal payment flow
☑ IDPay payment flow
☑ NextPay payment flow
☑ Gateway selection UI
☑ Callback handling
☑ Unit tests (80%+ coverage)
☑ Integration tests with sandbox
```

### Phase 4 Checklist

```
☑ All 31 provinces listed
☑ Shipping cost calculation
☑ Delivery time estimates
☑ Multiple shipping methods
☑ Insurance calculation
☑ Real-time pricing
```

### Phase 5 Checklist

```
☑ Tax calculation (9% VAT)
☑ Commodity price integration
☑ Regulation compliance
☑ Terms of Service (Persian)
☑ Privacy Policy (Persian)
☑ Legal review
```

---

## قضاو مسائل ممکنه

| Issue | Solution | Timeline |
|-------|----------|----------|
| کرانش بانکا | از VPN نمی‌رویم | - |
| پرتوول و اسقرو | API ترکیه با بانک | 1-2 weeks |
| رضایت راگذاری | مسه خودکار | 1 week |
| اعتبار بانکا | کسب تجویز مربوطه | 2-4 weeks |

---

## کمکه‌های لازمه

- [ ] Team familiar with Persian typography
- [ ] Android RTL expertise
- [ ] Payment gateway APIs knowledge
- [ ] Persian localization experience
- [ ] Iranian market knowledge

---

## Success Metrics

```
✓ RTL: 100% screens properly aligned
✓ Fonts: All text in Vazir/IranSans
✓ Payment: 3+ gateways working
✓ Coverage: 95%+ for Persian features
✓ Performance: <2s payment flow
✓ User satisfaction: >4.5 stars
```

---

## Next Steps

**This week:**
1. Start Phase 1 (RTL + Fonts)
2. Create test plan for Persian UI
3. Set up Zarinpal sandbox

**Next week:**
1. Complete Phase 1
2. Start Phase 2 (Dates/Numbers)
3. Begin Phase 3 research

---

پروژه برای **ایرانیان مقیم در ایران** تهيه می‌شود! 🇮🇷

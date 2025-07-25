<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>${storeName} - Privacy Policy</title>
  <style>
    body { font-family: Arial, sans-serif; line-height: 1.6; margin: 2rem; max-width: 800px; }
    h1, h2 { color: #333; }
    ul { margin-left: 1.5rem; }
    em { color: #666; }
  </style>
</head>
<body>
  <h1>Privacy Policy for ${storeName}</h1>
  <p><em>Last updated: July 24, 2025</em></p>

  <h2>1. Introduction</h2>
  <p>${storeName} (&quot;we,&quot; &quot;us,&quot; or &quot;our&quot;) respects your privacy. This Privacy Policy explains what information we collect, how we use it, and the choices you have about that information when you visit or make a purchase on <a href="https://the-little-store.herokuapp.com">the-little-store.herokuapp.com</a> (the &quot;Site&quot;).</p>

  <h2>2. Information We Collect</h2>
  <ul>
    <li><strong>Account Information.</strong> Name, email address, mailing address, and phone number when you register or place an order.</li>
    <li><strong>Payment Information.</strong> Usernames or identifiers for PayPal, Cash App, Venmo, Zelle, or Chime to process your order. We never collect full payment credentials; payments are handled by those services.</li>
    <li><strong>Order History & Cart Data.</strong> Your shopping cart and purchase history stored in our database.</li>
    <li><strong>Log Data.</strong> Technical data (e.g., IP address, browser type, request logs) collected via our hosting provider (Heroku) for security and troubleshooting.</li>
  </ul>

  <h2>3. How We Use Your Information</h2>
  <p>We use your personal data to:</p>
  <ul>
    <li>Create and manage your account;</li>
    <li>Process and fulfill your orders;</li>
    <li>Communicate order confirmations, shipping updates, and promotional messages (with your consent);</li>
    <li>Maintain and improve our Site and services;</li>
    <li>Detect and prevent fraud or other harmful activity.</li>
  </ul>

  <h2>4. Sharing & Disclosure</h2>
  <p>We share data only as needed with:</p>
  <ul>
    <li><strong>Payment processors</strong> (PayPal, Cash App, Venmo, Zelle, Chime);</li>
    <li><strong>Email service</strong> (Gmail API) for order and promotional emails;</li>
    <li><strong>Hosting & storage</strong> (Heroku, JawsDB for the database, Cloudinary for images).</li>
  </ul>
  <p>We do <strong>not</strong> sell or rent your personal information to third parties. We have no shipping or fulfillment partners sharing your data.</p>

  <h2>5. Cookies & Tracking</h2>
  <p>We do <strong>not</strong> use third-party analytics or marketing cookies. A session cookie may be set automatically to keep you logged in, but we do not track you across the web.</p>

  <h2>6. Data Retention</h2>
  <p>We retain your account data, order history, and payment identifiers indefinitely unless you request deletion.</p>

  <h2>7. Security</h2>
  <p>All data is transmitted over HTTPS. Passwords are hashed/encrypted in our database. We employ reasonable safeguards to protect your information.</p>

  <h2>8. Your Rights</h2>
  <ul>
    <li><strong>Access & Correction.</strong> You can view and edit your account details at <a href="${storeUrl}/account">${storeUrl}/account</a> when logged in.</li>
    <li><strong>Deletion.</strong> To delete your personal data, contact us (see Section 10).</li>
    <li><strong>Marketing Opt-out.</strong> Opt out of promotional emails via the &quot;unsubscribe&quot; link in any email.</li>
  </ul>

  <h2>9. Children&#39;s Privacy</h2>
  <p>Our Site is not directed at children under 13. We do not knowingly collect personal information from children.</p>

  <h2>10. Contact Us</h2>
  <p>If you have questions or wish to exercise your rights, email us at <a href="mailto:${receiverEmail}">${receiverEmail}</a>

  <h2>11. Applicable Law & Updates</h2>
  <p>We are based in Pipersville, Pennsylvania, USA, and this policy is governed by U.S. law. California residents have rights under the CCPA. EU residents may have GDPR rights. We may update this policy and will post changes here.</p>
</body>
</html>

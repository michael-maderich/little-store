<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/admin/fragments/adminHeader.jsp" %>

<h2>
  <c:choose>
    <c:when test="${isNew}">Create Product</c:when>
    <c:otherwise>Edit Product</c:otherwise>
  </c:choose>
</h2>

<!-- ==================
     0) GLOBAL ERROR BANNER (if controller set errorMessage)
     ================== -->
<c:if test="${not empty errorMessage}">
  <div class="alert alert-danger" style="margin-bottom:1rem;">
    ${errorMessage}
  </div>
</c:if>

<form:form id="productForm"
           modelAttribute="product"
           action="${contextPath}/admin/products/save"
           method="post"
           enctype="multipart/form-data">

  <!-- hidden flag so we know in the POST whether this is a create or an edit -->
  <input type="hidden" name="isNew" value="${isNew}" />

  <!-- ===================
       1) UPC (Primary Key, not nullable, unique)
       =================== -->
  <div class="form-group">
    <form:label path="upc"><b>UPC</b><span style="color:red;">*</span> (<i>required</i>)<br />Must be unique</form:label>
    <form:input path="upc"
                readonly="${!isNew}"
                cssClass="form-control"
                maxlength="${maxLenUpc}"
                pattern="^[A-Za-z0-9._-]+$"
                required="true" />
    <form:errors path="upc" cssClass="text-danger" />
  </div>

  <!-- ===================
       2) Main Category (not nullable) - dropdown with existing values + free entry
       =================== -->
  <div class="form-group">
    <form:label path="categoryMain"><b>Main Category</b><span style="color:red;">*</span> (<i>required</i>)<br />Select an existing category or add a new one</form:label>
  
    <!-- 1) the SELECT dropdown with all existing values + an "Other" choice -->
    <form:select path="categoryMain"
    			 cssClass="form-control"
    			 id="categoryMainSelect"
    			 required="true"
                 oninvalid="this.setCustomValidity('Please select or enter a main category')"
                 oninput="this.setCustomValidity('')">
      <form:option value="" label="-- Select Main Category --" />
      <c:forEach var="cm" items="${allCategoryMain}">
        <form:option value="${cm}">${cm}</form:option>
      </c:forEach>
      <form:option value="__OTHER__">New Main Category...</form:option>
    </form:select>
    <form:errors path="categoryMain" cssClass="text-danger" />

    <!-- 2) hidden text field, shown only when "Other" is chosen -->
    <div class="form-group" id="newCategoryMainDiv" style="display:none; margin-top:0.5rem;">
      <label for="newCategoryMain"><b><i>New Main Category</i></b></label>
      <input type="text"
             name="newCategoryMain"
             id="newCategoryMain"
             class="form-control"
             maxlength="${maxLenCategoryMain}"
             placeholder="Type new Main Category"
			 value="<c:out value='${param.newCategoryMain}'/>" />
      <span class="text-muted small">Enter a new category name.</span>
    </div>
  </div>

  <!-- ===================
       3) Secondary Category (nullable) - dropdown with existing Main Category values
       =================== -->
  <div class="form-group">
    <form:label path="categorySecondary"><b>Secondary Category</b> (optional)<br />Add another Category for product to show up under multiple categories</form:label>
  
    <form:select path="categorySecondary" cssClass="form-control" id="categorySecondarySelect">
      <!-- 1) "None" option if they don't want to pick a secondary category -->
      <form:option value="" label="-- None --" />
    
      <!-- 2) One <option> per existing value -->
      <c:forEach var="cs" items="${allCategorySecondary}">
        <form:option value="${cs}">${cs}</form:option>
      </c:forEach>
    </form:select>
  
    <!-- (No validation errors shown here, since this field is nullable) -->
  </div>

  <!-- ===================
       4) Specific Category (not nullable) - dropdown with existing values + free entry
       =================== -->
  <div class="form-group">
    <form:label path="categorySpecific"><b>Sub-Category</b><span style="color:red;">*</span> (<i>required</i>)<br />Select a Sub-category under selected Main Category or add a new one.</form:label>
  
    <!-- 1) the SELECT dropdown with all existing values + an "Other" choice -->
    <form:select path="categorySpecific"
    			 cssClass="form-control"
    			 id="categorySpecificSelect"
    			 required="true"
                 oninvalid="this.setCustomValidity('Please select or enter a specific category')"
                 oninput="this.setCustomValidity('')">
      <form:option value="" label="-- Select Sub-Category --" />
      <c:forEach var="csp" items="${allCategorySpecific}">
        <form:option value="${csp}">${csp}</form:option>
      </c:forEach>
      <form:option value="__OTHER__">New Sub-Category...</form:option>
    </form:select>
    <form:errors path="categorySpecific" cssClass="text-danger" />

    <!-- 2) hidden text field, shown only when "Other" is chosen -->
    <div class="form-group" id="newCategorySpecificDiv" style="display:none; margin-top:0.5rem;">
      <label for="newCategorySpecific"><b><i>New Sub-Category</i></b></label>
      <input type="text"
             name="newCategorySpecific"
             id="newCategorySpecific"
             class="form-control"
             maxlength="${maxLenCategorySpecific}"
			 placeholder="Type new Sub-Category"
       		 value="<c:out value='${param.newCategorySpecific}'/>" />
      <span class="text-muted small">Enter a brand-new sub-category name.</span>
    </div>
  </div>

  <!-- ===================
       5) Name (not nullable)
       =================== -->
  <div class="form-group">
    <form:label path="name"><b>Name</b><span style="color:red;">*</span> (<i>required</i>)<br />Brand name, usually (i.e. Tide, Suave, etc)</form:label>
    <form:input path="name" cssClass="form-control"  maxlength="${maxLenName}" required="true" />
    <form:errors path="name" cssClass="text-danger" />
  </div>

  <!-- ===================
       6) Options (nullable)
       =================== -->
  <div class="form-group">
    <form:label path="options"><b>Options</b> (optional)<br />Scent, flavor, etc.</form:label>
    <form:input path="options" cssClass="form-control"  maxlength="${maxLenOptions}" />
  </div>

  <!-- ===================
       7) Size (nullable)
       =================== -->
  <div class="form-group">
    <form:label path="size"><b>Size</b> (optional)<br />12 oz, 20 g, 1 ct, etc.</form:label>
    <form:input path="size" cssClass="form-control"  maxlength="${maxLenSize}" />
  </div>

  <!-- ===================
       8) Cost (float, nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="cost"><b>Cost</b> (optional)<br />Unit cost per item added</form:label>
    <form:input path="cost" type="number" inputmode="decimal" cssClass="form-control" step="0.0001" min="0" max="10000" />
    <form:errors path="cost" cssClass="text-danger" />
  </div>

  <!-- ===================
       9) Retail Price (float, nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="retailPrice"><b>Retail Price</b> (optional)<br />Lowest retail price for user comparison (i.e. Walmart Price)</form:label>
    <div class="currency-container">
      <span class="currency-symbol">$</span>
      <form:input path="retailPrice" type="number" inputmode="decimal" cssClass="form-control" step="0.01" min="0" max="10000" />
    </div>
    <form:errors path="retailPrice" cssClass="text-danger" />
  </div>

  <!-- ===================
       10) Base Price (float, not nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="basePrice"><b>Base Price</b><span style="color:red;">*</span> (<i>required</i>)<br />Regular price for item (as opposed to Current Price which may be sale price)</form:label>
    <div class="currency-container">
      <span class="currency-symbol">$</span>
      <form:input path="basePrice" type="number" inputmode="decimal" cssClass="form-control" step="0.0001" min="0" max="10000" required="true" />
    </div>
    <form:errors path="basePrice" cssClass="text-danger" />
  </div>

  <!-- ===================
       11) Current Price (float, not nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="currentPrice"><b>Current Price</b><span style="color:red;">*</span> (<i>required</i>)<br />Current selling price (Base Price or less)</form:label>
    <div class="currency-container">
      <span class="currency-symbol">$</span>
      <form:input path="currentPrice" type="number" inputmode="decimal" cssClass="form-control" step="0.0001" min="0" max="10000" required="true" />
    </div>
    <form:errors path="currentPrice" cssClass="text-danger" />
  </div>

  <!-- ===================
       12) Stock Quantity (int, not nullable)
       =================== -->
  <div class="form-group">
    <form:label path="stockQty"><b>Stock Quantity</b><span style="color:red;">*</span> (<i>required</i>)<br />Current stock quantity - increase to add new stock</form:label>
    <form:input path="stockQty" type="number" cssClass="form-control" step="1" min="0" max="10000" required="true" />
    <form:errors path="stockQty" cssClass="text-danger" />
  </div>

  <!-- ===================
       13) Purchase Limit (int, nullable)
       =================== -->
  <div class="form-group">
    <form:label path="purchaseLimit"><b>Purchase Limit</b> (optional)<br />Purchase Limit per order per customer. Leave blank or 0 for no limit.</form:label>
    <form:input path="purchaseLimit" type="number" cssClass="form-control" step="1" min="0" max="10000" />
  </div>

  <!-- ===================
       14) Image (URL stored in DB, not nullable) and upload field
       =================== -->
  <div class="form-group">
    <label for="imageFile"><b>Product Image</b><span style="color:red;">*</span> (<i>required</i>) Max size: 5 MB<br />If image has a transparent background (auto-detected), it will be randomly shown at login, etc.</label>
    <input type="file"
           name="imageFile"
           id="imageFile"
           accept="image/*"
           class="form-control"
           <c:if test="${isNew}">required="true"</c:if>
           maxlength="255" />
    <form:errors path="image" cssClass="text-danger" />
  </div>

  <!-- ===================
       15) Current Image + Transparent checkbox (only if editing/existing product)
       =================== -->
  <!--c:if test="${not empty product.image}"-->
    <div class="form-group d-flex align-items-center" style="justify-content: center; gap: 1rem; margin-top: 1rem;">
      <!-- 1) Current Image Preview (on the left) -->
      <div style="text-align: center;">
        <c:choose><c:when test="${not empty product.image}">
          <img src="${product.image}"
               alt="${product.name}"
               loading="lazy"
               class="product-img-preview" />
        </c:when><c:otherwise>
          <!-- Placeholder if no image set -->
          <img src="${contextPath}/images/placeholder.jpg"
               alt="No image"
               class="product-img-preview" />
        </c:otherwise></c:choose>
      </div>

      <!-- 2) "Transparent Background" Checkbox (on the right) -->
      <div class="form-check" style="display: flex; align-items: center;">
        <form:hidden path="transparent" />
        <form:checkbox path="transparent"
                       cssClass="form-check-input"
                       id="transparent" />
        <label class="check-label"
               for="transparent">
          Transparent Background
        </label>
      </div>
    </div>
    <p style="margin-top: 0.5rem;">
      <small><b>Current image</b> (will be replaced if you upload a new file)</small>
    </p>
  <!--/c:if-->

  <!-- 16) Hidden fields we do not expose: onSale (computed), inventoried, inventoriedDate,
      description (computed), dateAdded (computed), dateLastSold (not updated here) -->

  <button type="submit" class="btn btn-primary">Save</button>
  <a href="${contextPath}/admin/products" class="btn btn-secondary">Cancel</a>
</form:form>

<script>
document.addEventListener("DOMContentLoaded", function() {
  // grab everything once
  const mainSelect = document.getElementById("categoryMainSelect");
  const mainDiv    = document.getElementById("newCategoryMainDiv");
  const mainInput  = document.getElementById("newCategoryMain");
  const specSelect = document.getElementById("categorySpecificSelect");
  const specDiv    = document.getElementById("newCategorySpecificDiv");
  const specInput  = document.getElementById("newCategorySpecific");
  const form       = document.getElementById("productForm");

  // Toggle functions
  function toggleMainField() {
    console.log("toggleMainField:", mainSelect.value);
    if (mainSelect.value === "__OTHER__") {
      mainDiv.style.display  = "block";
      mainInput.required     = true;
    } else {
      mainDiv.style.display  = "none";
      mainInput.required     = false;
      mainInput.value        = "";
    }
  }
  function toggleSpecField() {
    console.log("toggleSpecField:", specSelect.value);
    if (specSelect.value === "__OTHER__") {
      specDiv.style.display  = "block";
      specInput.required     = true;
    } else {
      specDiv.style.display  = "none";
      specInput.required     = false;
      specInput.value        = "";
    }
  }

  // wire up the change events
  mainSelect.addEventListener("change", toggleMainField);
  specSelect.addEventListener("change", toggleSpecField);

  // run once on load (covers validation-error re-render)
  toggleMainField();
  toggleSpecField();

  // client-side regex for final validation
  const clientRegex = /^[A-Za-z0-9]([A-Za-z0-9._-]*[A-Za-z0-9])?$/;

  form.addEventListener("submit", function(e) {
    // only bother if the free-form field is visible
    if (mainSelect.value === "__OTHER__") {
      const v = mainInput.value.trim();
      if (!clientRegex.test(v)) {
        e.preventDefault();
        alert("Main category name must start/end with a letter or digit, may only contain letters, digits, hyphens (-), dots (.), or underscores (_), and no slashes.");
        mainInput.focus();
        return;
      }
    }
    if (specSelect.value === "__OTHER__") {
      const v = specInput.value.trim();
      if (!clientRegex.test(v)) {
        e.preventDefault();
        alert("Specific category name must start/end with a letter or digit, may only contain letters, digits, hyphens (-), dots (.), or underscores (_), and no slashes.");
        specInput.focus();
        return;
      }
    }
  });
});
</script>

<%@ include file="/WEB-INF/views/admin/fragments/adminFooter.jsp" %>
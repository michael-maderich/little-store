<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/WEB-INF/views/admin/fragments/adminHeader.jsp" %>

<h2>
  <c:choose>
    <c:when test="${product.upc == null}">
      Create Product
    </c:when>
    <c:otherwise>
      Edit Product
    </c:otherwise>
  </c:choose>
</h2>

<form:form modelAttribute="product"
           action="${contextPath}/admin/products/save"
           method="post"
           enctype="multipart/form-data">

  <!-- ===================
       1) UPC (Primary Key, not nullable, unique)
       =================== -->
  <div class="form-group">
    <form:label path="upc"><b>UPC</b><span style="color:red;">*</span> (required)<br />Must be unique</form:label>
    <form:input path="upc"
                readonly="${not empty product.upc}"
                cssClass="form-control" />
    <form:errors path="upc" cssClass="text-danger" />
  </div>

  <!-- ===================
       2) Main Category (not nullable) - dropdown with existing values + free entry
       =================== -->
  <div class="form-group">
    <form:label path="categoryMain"><b>Main Category</b><span style="color:red;">*</span> (required)<br />Select an existing category or add a new one</form:label>
  
    <!-- 1) the SELECT dropdown with all existing values + an "Other" choice -->
    <form:select path="categoryMain" cssClass="form-control" id="categoryMainSelect">
      <form:option value="" label="-- Select Main Category --" />
      <c:forEach var="cm" items="${allCategoryMain}">
        <form:option value="${cm}">${cm}</form:option>
      </c:forEach>
      <form:option value="__OTHER__">Enter New Main Category...</form:option>
    </form:select>
    <form:errors path="categoryMain" cssClass="text-danger" />

    <!-- 2) hidden text field, shown only when "Other" is chosen -->
    <div class="form-group" id="newCategoryMainDiv" style="display:none; margin-top:0.5rem;">
      <label for="newCategoryMain"><b><i>New Main Category</i></b></label>
      <input type="text"
             name="newCategoryMain"
             id="newCategoryMain"
             class="form-control"
             placeholder="Type new Main Category" />
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
    <form:label path="categorySpecific"><b>Sub-Category</b><span style="color:red;">*</span> (required)<br />Select a Sub-category under selected Main Category or add a new one.</form:label>
  
    <!-- 1) the SELECT dropdown with all existing values + an "Other" choice -->
    <form:select path="categorySpecific" cssClass="form-control" id="categorySpecificSelect">
      <form:option value="" label="-- Select Sub-Category --" />
      <c:forEach var="csp" items="${allCategorySpecific}">
        <form:option value="${csp}">${csp}</form:option>
      </c:forEach>
      <form:option value="__OTHER__">Other...</form:option>
    </form:select>
    <form:errors path="categorySpecific" cssClass="text-danger" />

    <!-- 2) hidden text field, shown only when "Other" is chosen -->
    <div class="form-group" id="newCategorySpecificDiv" style="display:none; margin-top:0.5rem;">
      <label for="newCategorySpecific"><b><i>New Sub-Category</i></b></label>
      <input type="text"
             name="newCategorySpecific"
             id="newCategorySpecific"
             class="form-control"
             placeholder="Type new Sub-Category" />
      <span class="text-muted small">Enter a brand-new category name.</span>
    </div>
  </div>

  <!-- ===================
       5) Name (not nullable)
       =================== -->
  <div class="form-group">
    <form:label path="name"><b>Name</b><span style="color:red;">*</span> (required)<br />Brand name, usually (i.e. Tide, Suave, etc)</form:label>
    <form:input path="name" cssClass="form-control" />
    <form:errors path="name" cssClass="text-danger" />
  </div>

  <!-- ===================
       6) Options (nullable)
       =================== -->
  <div class="form-group">
    <form:label path="options"><b>Options</b><br />Scent, flavor, etc.</form:label>
    <form:input path="options" cssClass="form-control" />
  </div>

  <!-- ===================
       7) Size (nullable)
       =================== -->
  <div class="form-group">
    <form:label path="size"><b>Size</b><br />12 oz, 20 g, 1 ct, etc.</form:label>
    <form:input path="size" cssClass="form-control" />
  </div>

  <!-- ===================
       8) Cost (float, nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="cost"><b>Cost</b> (optional)<br />Unit cost per item added</form:label>
    <form:input path="cost" type="number" step="0.01" cssClass="form-control" />
    <form:errors path="cost" cssClass="text-danger" />
  </div>

  <!-- ===================
       9) Retail Price (float, nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="retailPrice"><b>Retail Price</b> (optional)<br />Lowest retail price for user comparison (i.e. Walmart Price)</form:label>
    <form:input path="retailPrice" type="number" step="0.01" cssClass="form-control" />
    <form:errors path="retailPrice" cssClass="text-danger" />
  </div>

  <!-- ===================
       10) Base Price (float, not nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="basePrice"><b>Base Price</b><span style="color:red;">*</span> (required)<br />Regular price for item (as opposed to Current Price which may be sale price)</form:label>
    <form:input path="basePrice" type="number" step="0.01" cssClass="form-control" />
    <form:errors path="basePrice" cssClass="text-danger" />
  </div>

  <!-- ===================
       11) Current Price (float, not nullable) - currency
       =================== -->
  <div class="form-group">
    <form:label path="currentPrice"><b>Current Price</b><span style="color:red;">*</span> (required)<br />Current selling price (Base Price or less)</form:label>
    <form:input path="currentPrice" type="number" step="0.01" cssClass="form-control" />
    <form:errors path="currentPrice" cssClass="text-danger" />
  </div>

  <!-- ===================
       12) Stock Quantity (int, not nullable)
       =================== -->
  <div class="form-group">
    <form:label path="stockQty"><b>Stock Quantity</b><span style="color:red;">*</span> (required)<br />Current stock quantity - increase to add new stock</form:label>
    <form:input path="stockQty" type="number" cssClass="form-control" />
    <form:errors path="stockQty" cssClass="text-danger" />
  </div>

  <!-- ===================
       13) Purchase Limit (int, nullable)
       =================== -->
  <div class="form-group">
    <form:label path="purchaseLimit"><b>Purchase Limit</b> (optional)<br />Purchase Limit per order per customer. Leave blank or 0 for no limit.</form:label>
    <form:input path="purchaseLimit" type="number" cssClass="form-control" />
  </div>

  <!-- ===================
       14) Image (URL stored in DB, not nullable) and upload field
       =================== -->
  <div class="form-group">
    <label for="imageFile"><b>Product Image</b><span style="color:red;">*</span> (required)<br />Image required for new items or replace for existing items.</label>
    <input type="file"
           name="imageFile"
           id="imageFile"
           accept="image/*"
           class="form-control" />
    <br /> Check <i>Transparent</i> if image has a transparent background. It will be randomly shown at login, etc.
    <form:errors path="image" cssClass="text-danger" />

    <!-- ===================
       15) Transparent (boolean, not nullable) - rendered as a checkbox
       =================== -->
    <div class="form-group form-check">
      <form:checkbox path="transparent" cssClass="form-check-input" id="transparent" />
      <label class="form-check-label" for="transparent"><b>Transparent?</b></label>
      <form:errors path="transparent" cssClass="text-danger" />
    </div>

    <c:if test="${not empty product.image}">
      <br/>
      <img src="${product.image}"
           alt="${product.name}"
           width="120"
           height="120"
           loading="lazy" />

      <p><small><b>Current image</b> (will be replaced if you choose a new file)</small></p>
    </c:if>
  </div>

  <!-- 16) Hidden fields we do not expose: onSale, inventoried, inventoriedDate,
      description (computed), dateAdded (computed), dateLastSold (not updated here) -->

  <button type="submit" class="btn btn-primary">
    Save
  </button>
  <a href="${contextPath}/admin/products" class="btn btn-secondary">
    Cancel
  </a>
</form:form>

<script>
  // Whenever the Main Category <select> changes...
  document.getElementById('categoryMainSelect').addEventListener('change', function(e) {
    const newDiv = document.getElementById('newCategoryMainDiv');
    if (this.value === '__OTHER__') {
      newDiv.style.display = 'block';
    } else {
      newDiv.style.display = 'none';
      // Clear the "other" text field if user switches back to a real option
      document.getElementById('newCategoryMain').value = '';
    }
  });

  // Whenever the Specific Category <select> changes...
  document.getElementById('categorySpecificSelect').addEventListener('change', function(e) {
    const newDiv = document.getElementById('newCategorySpecificDiv');
    if (this.value === '__OTHER__') {
      newDiv.style.display = 'block';
    } else {
      newDiv.style.display = 'none';
      document.getElementById('newCategorySpecific').value = '';
    }
  });

  // On page load, if the bound value is "Other", show its text field
  window.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('categoryMainSelect').value === '__OTHER__') {
      document.getElementById('newCategoryMainDiv').style.display = 'block';
    }
    if (document.getElementById('categorySpecificSelect').value === '__OTHER__') {
      document.getElementById('newCategorySpecificDiv').style.display = 'block';
    }
  });
</script>

<%@ include file="/WEB-INF/views/admin/fragments/adminFooter.jsp" %>
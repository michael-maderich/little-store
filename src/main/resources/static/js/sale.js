/**
 * 
 */
function updateRows(upc)
{
	var rows = document.getElementsByClassName("qtyInput");
	var updatedQty = document.getElementById(`itemQty${upc}`);
	if (!updatedQty || !updatedQty.value || parseInt(updatedQty.value) == 0)
	{
		for (let i = 0; i < rows.length; i++) {
			rows[i].disabled=false;
		}
		document.getElementById(`addBtn${upc}`).style.visibility='hidden';
	}
	else
	{
		for (let i = 0; i < rows.length; i++) {
			if (rows[i].id != `itemQty${upc}`) rows[i].disabled=true;
		}
		document.getElementById(`addBtn${upc}`).style.visibility='visible';
	}
}
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head profile="http://selenium-ide.openqa.org/profiles/test-case">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<link rel="selenium.base" href="http://vaadin-integration-tests:8080/" />
<title>addressbook_deployment</title>
</head>
<body>
<table cellpadding="1" cellspacing="1" border="1">
<thead>
<tr><td rowspan="1" colspan="3">addressbook_deployment</td></tr>
</thead><tbody>
<tr>
	<td>openAndWait</td>
	<td>/demo/AddressBook</td>
	<td></td>
</tr>
<tr>
	<td>mouseClick</td>
	<td>vaadin=demoAddressBook::/VVerticalLayout[0]/ChildComponentContainer[1]/VSplitPanelHorizontal[0]/VSplitPanelVertical[0]/VScrollTable[0]/domChild[1]/domChild[0]/domChild[1]/domChild[0]/domChild[2]/domChild[0]/domChild[0]</td>
	<td>62,13</td>
</tr>
<tr>
	<td>pause</td>
	<td>500</td>
	<td></td>
</tr>
<tr>
	<td>screenCapture</td>
	<td></td>
	<td>Marge_selected</td>
</tr>
<tr>
	<td>click</td>
	<td>vaadin=demoAddressBook::/VVerticalLayout[0]/ChildComponentContainer[1]/VSplitPanelHorizontal[0]/VSplitPanelVertical[0]/VForm[0]/VHorizontalLayout[0]/ChildComponentContainer[2]/VButton[0]/domChild[0]</td>
	<td></td>
</tr>
<tr>
	<td>pause</td>
	<td>500</td>
	<td></td>
</tr>
<tr>
	<td>screenCapture</td>
	<td></td>
	<td>Marge_in_edit_mode</td>
</tr>

</tbody></table>
</body>
</html>

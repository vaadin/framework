<tr>
	<td>waitForElementPresent</td>
	<td>//div[2]/div/div/div/span</td>
	<td></td>
</tr>
<tr>
	<td>assertText</td>
	<td>//div[2]/div/div/div/span</td>
	<td>Test of ApplicationResources with full path</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//body/div[2]/div//p</td>
	<td>exact:Mode: view State: normal</td>
</tr>
<tr>
	<td>screenCapture</td>
	<td></td>
	<td>initial</td>
</tr>
<tr>
	<td>closeNotification</td>
	<td>//body/div[2]/div</td>
	<td>0,0</td>
</tr>
<tr>
	<td>assertTextNotPresent</td>
	<td>Action * received</td>
	<td></td>
</tr>
<tr>
	<td>assertText</td>
	<td>//div[@class=&quot;v-link v-widget&quot;]/a/span</td>
	<td>Edit</td>
</tr>
<!--Send an action-->
<tr>
	<td>mouseClickAndWait</td>
	<td>//div[8]/div/a/span</td>
	<td>32,9</td>
</tr>
<tr>
	<td>assertTextPresent</td>
	<td>Action 'someAction' received</td>
	<td></td>
</tr>
<tr>
	<td>assertText</td>
	<td>//body/div[2]/div//p</td>
	<td>exact:Mode: view State: normal</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//div[@class=&quot;v-link v-widget&quot;]/a/span</td>
	<td>Edit</td>
</tr>
<tr>
	<td>closeNotification</td>
	<td>//body/div[2]/div</td>
	<td>0,0</td>
</tr>
<!--Switch to edit mode-->
<tr>
	<td>mouseClickAndWait</td>
	<td>//div[5]/div/a/span</td>
	<td>12,3</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//body/div[2]/div//p</td>
	<td>exact:Mode: edit State: normal</td>
</tr>
<tr>
	<td>closeNotification</td>
	<td>//body/div[2]/div</td>
	<td>0,0</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//div[@class=&quot;v-link v-widget&quot;]/a/span</td>
	<td>Done</td>
</tr>
<!--Maximize-->
<tr>
	<td>mouseClickAndWait</td>
	<td>//div[6]/div/a/span</td>
	<td>16,7</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//body/div[2]/div//p</td>
	<td>exact:Mode: edit State: maximized</td>
</tr>
<tr>
	<td>closeNotification</td>
	<td>//body/div[2]/div</td>
	<td>0,0</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//div[6]/div/a/span</td>
	<td>Back to normal</td>
</tr>
<!--Restore back to normal-->
<tr>
	<td>mouseClickAndWait</td>
	<td>//div[6]/div/a/span</td>
	<td>71,8</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//body/div[2]/div//p</td>
	<td>exact:Mode: edit State: normal</td>
</tr>
<tr>
	<td>closeNotification</td>
	<td>//body/div[2]/div</td>
	<td>0,0</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//div[@class=&quot;v-link v-widget&quot;]/a/span</td>
	<td>Done</td>
</tr>
<tr>
	<td>assertText</td>
	<td>//div[6]/div/a/span</td>
	<td>Maximize</td>
</tr>
<tr>
	<td>screenCapture</td>
	<td></td>
	<td>final</td>
</tr>
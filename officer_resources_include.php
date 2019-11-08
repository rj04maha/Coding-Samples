<style>
#banner-style {
	font-size: 10pt;
	width: 100%;
	text-align: left;
	line-height: 20px;
	vertical-align: middle;
	border-top: 2px solid #cccccc;
	border-bottom: 2px solid #cccccc;
}

#button:hover {
	cursor: pointer;
	background-color: #0960ab;
	transition: .6s background-color;
}


#button {
	background-color: #052d4f;
	border-radius:7px;
	display:inline-block;
	color:#ffffff;
	font-size:14px;
	padding:16px 41px;
	width: 20%;
	margin:5px;
}

</style>


<?php
// CHECK USER'S OFFICE TITLE
$curl = curl_init();
curl_setopt_array($curl, array(
//CURLOPT_URL => "http://webapi.cseainc.org:1337/officer_resources/vipinfo/". $_SESSION['csea_id'],
CURLOPT_URL => $GLOBALS['api_url'] . "officer_resources/vipinfo/". $_SESSION['csea_id'],
CURLOPT_RETURNTRANSFER => true,
CURLOPT_TIMEOUT => 30,
CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
CURLOPT_CUSTOMREQUEST => "GET",
CURLOPT_HTTPHEADER => array(
	"cache-control: no-cache"
),
));

$response = curl_exec($curl);
$err = curl_error($curl);
curl_close($curl);
$val = json_decode($response);


if ($val[0][0]){
	$title="";
	for($i=0; $i < count($val); $i++)
	{
		if($val[$i][0]=='R' || $val[$i][0]=='L' || $val[$i][0]=='U' || $val[$i][0]=='S')
		{
			// Turn these into codes we want
			if($val[$i][1]=='PRES')
				$title = "PRES";
			if(stristr($val[$i][1],"VP"))
				$title = "VP";
			if($val[$i][1]=='RESE' ||  $val[$i][1]=='SETR' ||  $val[$i][1]=='CRSE' ||  $val[$i][1]=='VPSE')
				$title = "RESE";
			if($val[$i][1]=='TRES' ||  $val[$i][1]=='SETR')
				$title = "TRES";
		}
		if($val[$i][0]=='G')
		{
			$title = "GR";
		}
		if($val[$i][0]=='W')
		{
			$title = "STEW";
		}
		if($val[$i][0]=='C' || $val[$i][1]=='****')
		{
			$title = "ALL";
		}
	}

	// FAQS LINKS
	echo "<br /><a href='".$GLOBALS['url']."wp-content/uploads/2019/09/FAQsLocalUnitLeaders.pdf' target='_blank'><b>FAQs for Local/Unit Leaders - PDF version</a></b><br /><br />";
	echo "<a href='".$GLOBALS['url']."wp-content/uploads/2019/09/FAQsLocalUnitLeaders.doc' target='_blank'><b>FAQs for Local/Unit Leaders - MS Word version</a></b><br /><br />";

	// BANNER

	$curl = curl_init();

	curl_setopt_array($curl, array(
	//CURLOPT_URL => "http://webapi.cseainc.org:1337/officer_resources/banners",
	CURLOPT_URL => $GLOBALS['api_url'] . "officer_resources/banners",
	CURLOPT_RETURNTRANSFER => true,
	CURLOPT_TIMEOUT => 30,
	CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
	CURLOPT_CUSTOMREQUEST => "GET",
	CURLOPT_HTTPHEADER => array(
		"cache-control: no-cache"
	),
	));

	$response = curl_exec($curl);
	$err = curl_error($curl);
	curl_close($curl);

	$val = json_decode($response);


	for($i=0; $i < count($val); $i++)
	{
		if($val[$i][1]=='ALL')
			$all_banner=html_entity_decode($val[$i][2]);
		if($val[$i][1]=='PRES')
			$pres_banner=html_entity_decode($val[$i][2]);
		if($val[$i][1]=='VP')
			$vp_banner=html_entity_decode($val[$i][2]);
		if($val[$i][1]=='RESE')
			$rese_banner=html_entity_decode($val[$i][2]);
		if($val[$i][1]=='TRES')
			$tres_banner=html_entity_decode($val[$i][2]);
		if($val[$i][1]=='GR')
			$gr_banner=html_entity_decode($val[$i][2]);
		if($val[$i][1]=='STEW')
			$stew_banner=html_entity_decode($val[$i][2]);
	}
	if($all_banner)
	{
		echo "<div id='banner-style'>$all_banner</div>";
	}
	if($pres_banner && ($title == "PRES" || $title == "ALL"))
	{
		echo "<div id='banner-style'>$pres_banner</div>";
	}
	if($vp_banner && ($title == "VP" || $title == "ALL"))
	{
		echo "<div id='banner-style'>$vp_banner</div>";
	}
	if($rese_banner && ($title == "RESE" || $title == "ALL"))
	{
		echo "<div id='banner-style'>$rese_banner</div>";
	}
	if($tres_banner && ($title == "TRES" || $title == "ALL"))
	{
		echo "<div id='banner-style'>$tres_banner</div>";
	}
	if($gr_banner && ($title == "GR" || $title == "ALL"))
	{
		echo "<div id='banner-style'>$gr_banner</div>";
	}
	if($stew_banner && ($title == "STEW" || $title == "ALL"))
	{
		echo "<div id='banner-style'>$stew_banner</div>";
	}

	echo "<br/>";


	// DROP DOWN MENU

	// Use this to test different 
	//$title = "TRES";

	$curl = curl_init();

	curl_setopt_array($curl, array(
	//CURLOPT_URL => "http://webapi.cseainc.org:1337/officer_resources/titles/" . $title,
	CURLOPT_URL => $GLOBALS['api_url'] . "officer_resources/titles/" . $title,
	CURLOPT_RETURNTRANSFER => true,
	CURLOPT_TIMEOUT => 30,
	CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
	CURLOPT_CUSTOMREQUEST => "GET",
	CURLOPT_HTTPHEADER => array(
		"cache-control: no-cache"
	),
	));

	$response = curl_exec($curl);
	$err = curl_error($curl);
	curl_close($curl);
	$response = json_decode($response);

	if(count($response) > 0)
	{
		echo "<select name='officer_select' id='resource_select' size=1 onchange='changeDesc();'>";
		echo "<option value='sel'>[SELECT RESOURCE]</option>";
		for($i=0; $i < count($response); $i++)
		{
			echo "<option value='". $response[$i][0] . "~~" . $response[$i][1] . "~~" . $response[$i][2] . "~~" . $response[$i][5] . "'>" . $response[$i][6] . "</option>";
		}
		echo "</select><br /><br />";
		echo "<div id='resource_description'></div><br/>";
		echo "<div id='resource-count'><b>" . count($response) . " resources found.</b></div><br />";
	}
	else
	{
		echo "<b><font color=\"#993333\">There are no resources currently available.</b><br /><br />";
	}
}
	else{
		echo "You are not authorized to view this page.";
	}




?>

<script type="text/javascript">
function changeDesc()
{
	// access php global variable
	var globalURL = '<?php echo $GLOBALS['url']; ?>';

	// AJAX to get the descriptions for resources
		var str=document.getElementById('resource_select').options[document.getElementById('resource_select').selectedIndex].value;
		
		var res = str.split("~~", 4);
 		var ID = res[0];
		var type = res[1];
		var desc = res[2];
		var link = res[3];




/* 
		if(type=="P")
		{
			text="<b>Format:</b> PDF<br/>";
			//text+="<a href=\"" + globalURL + "applications/viewer.php?resource_id=" + ID + "?apiurl=" $GLOBALS['url'] + "\" target='_blank'>Click here to download</a><br/>";
		}
		if(type=="D")
		{
			text="<b>Format:</b> Word/text document<br/>";
			//text+="<a href=\"" + globalURL + "applications/viewer.php?resource_id=" + ID + "?apiurl=" $GLOBALS['url'] + "\" target='_blank'>Click here to download</a><br/>";
		}
		if(type=="S")
		{
			text="<b>Format:</b> Excel spreadsheet<br/>";
			//text+="<a href=\"" + globalURL + "applications/viewer.php?resource_id=" + ID + "?apiurl=" $GLOBALS['url'] + "\" target='_blank'>Click here to download</a><br/>";
		} */


		if(type=="H")
		{
			text="<b>Format:</b> Web page link<br/>";
			text+="<a href='"+ link + "' target='_blank'>Click here to go to web page</a><br/>";
		} 
		else {
			text = "<form action='" + globalURL + "applications/viewer.php?resource_id=" + ID + "' method='post'><tr><td><input type='hidden' name='globalAPI' id='globalAPI' value='<?php echo $GLOBALS['api_url']; ?>' /></td></tr><br/><tr><td><input id='button' type='submit' value='Click here to download'></td></tr></form>";
		}


		



		if (!desc)
		{
			desc = "This resource does not have a description."
		}

		if (ID == 'sel')
		{
			document.getElementById('resource_description').innerHTML= "<b>Please select a resource</b>";
		}
		else{
		document.getElementById('resource_description').innerHTML="<b>RESOURCE DESCRIPTION:</b><br/>" + desc + "<br/>" + text;
		}
}

function filterResults()
{
	document.getElementById('resource_select').length=1;
	document.getElementById('resource_select').options[0]=new Option('[SELECT RESOURCE]','');
}
</script>

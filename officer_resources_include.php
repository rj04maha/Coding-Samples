<!--
I decided to use this PHP script as an example of code I wrote at CSEA. This file is located on the webserver that hosts CSEA's future 
Wordpress website. The purpose of this webpage is to display resources that are available to Union officers based on their ranking. It calls 
the API twice, once to check the officer status of the user and another to retrieve the resources that each officer has access to. It also uses 
Javascript to change the description of the resource before the user downloads it. I chose this code as my sample not because of I am super 
proud of it but I acknowledge it can be improved. 

While writing this code, I was under restrictions from my team. I was told I cannot use Javascript to make a request to the API. I would have 
loved to use AJAX to make aditional calls to the API to retrieve the resource description but instead I had to get creative in the way I pass 
data (that was retrieved using PHP) to be used in Javascript to change the description of the resource.
-->

<?php
// Check user's office title using CSEA ID that is stored in a session variable and API URL that is stored in a global variable and API 
endpoint
$curl = curl_init();
curl_setopt_array($curl, array(
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

// If the API returns an array with something in the first index
if ($val[0][0]) {
	$title="";
	for($i=0; $i < count($val); $i++) {
		if($val[$i][0]=='R' || $val[$i][0]=='L' || $val[$i][0]=='U' || $val[$i][0]=='S') {
			// Turn these into codes we will use to make another API request
			if($val[$i][1]=='PRES')
				$title = "PRES";
			if(stristr($val[$i][1],"VP"))
				$title = "VP";
			if($val[$i][1]=='RESE' ||  $val[$i][1]=='SETR' ||  $val[$i][1]=='CRSE' ||  $val[$i][1]=='VPSE')
				$title = "RESE";
			if($val[$i][1]=='TRES' ||  $val[$i][1]=='SETR')
				$title = "TRES";
		}
		if($val[$i][0]=='G') {
			$title = "GR";
		}
		if($val[$i][0]=='W') {
			$title = "STEW";
		}
		if($val[$i][0]=='C' || $val[$i][1]=='****') {
			$title = "ALL";
		}
	}

	// DROP DOWN MENU

	// Uncomment this line to to test different titles
	//$title = "TRES";

	$curl = curl_init();

	curl_setopt_array($curl, array(
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

	// As long as there is one resource
	if(count($response) > 0) {
		echo "<select name='officer_select' id='resource_select' size=1 onchange='changeDesc();'>";
		echo "<option value='sel'>[SELECT RESOURCE]</option>";
		// For each resource, an option is created
		for($i=0; $i < count($response); $i++)
		{
			// The '~~' was a way I am able to pass data to JS since I cannot use AJAX to call the API again
			echo "<option value='". $response[$i][0] . "~~" . $response[$i][1] . "~~" . $response[$i][2] . "~~" . $response[$i][5] . "'>" . $response[$i][6] . "</option>";
		}
		echo "</select><br /><br />";
		echo "<div id='resource_description'></div><br/>";
		echo "<div id='resource-count'><b>" . count($response) . " resources found.</b></div><br />";
	}
	else {
		echo "<b><font color=\"#993333\">There are no resources currently available.</b><br /><br />";
	}
}
// If the user is not an officer
else {
	echo "You are not authorized to view this page.";
}

?>

<script type="text/javascript">
function changeDesc()
{
	// Access php global variable for later
	var globalURL = '<?php echo $GLOBALS['url']; ?>';

	// Get the descriptions for resources
	var str=document.getElementById('resource_select').options[document.getElementById('resource_select').selectedIndex].value;

	// Had to pass data from PHP to JS since I am not able to make a request to the API using AJAX
	var res = str.split("~~", 4);
	var ID = res[0];
	var type = res[1];
	var desc = res[2];
	var link = res[3];

	// H represents a webpage
	if(type=="H") {
		text="<b>Format:</b> Web page link<br/>";
		text+="<a href='"+ link + "' target='_blank'>Click here to go to web page</a><br/>";
	} 
	// If not a webpage, then it is a file that needs to be downloaded
	// veiwer.php will download the file for the user & displays a button to user to download
	else {
		text = "<form action='" + globalURL + "applications/viewer.php?resource_id=" + ID + "' method='post'><tr><td><input id='button' type='submit' value='Click here to download'></td></tr></form>";
	}

	if (!desc) {
		desc = "This resource does not have a description."
	}

	if (ID == 'sel') {
		document.getElementById('resource_description').innerHTML= "<b>Please select a resource</b>";
	}
	// Displays resource description
	else{
	document.getElementById('resource_description').innerHTML="<b>RESOURCE DESCRIPTION:</b><br/>" + desc + "<br/>" + text;
	}
}
</script>

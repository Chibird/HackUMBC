<?php
//connect to database
$con=mysqli_connect("54.165.53.129","root","blargHblargh1");

//Create table;
$lat = htmlspecialchars($_GET["lat"]);
$lng = htmlspecialchars($_GET["lng"]);
$name = htmlspecialchars($_GET["name"]);
$isMoving = htmlspecialchars($_GET["isMoving"]);
$bearing = htmlspecialchars($_GET["bearing"]);

mysqli_query($con, "USE marauder_map");
mysqli_query($con, "INSERT INTO marauder_map (name, isMoving, lat, lng, bearing) VALUES ('$name', 'true', '$lat','$lng','$bearing')");

$table = mysqli_query($con, "SELECT MAX(id) AS id FROM marauder_map");
$row = mysqli_fetch_array($table);
$id = $row['id'];
echo $id;
?>

<?php

//connect to database
$con=mysqli_connect("54.165.53.129","root","blargHblargh1");

if(mysqli_connect_errno()){
  echo "failed to connect to MySQL: " . mysqli_connect_error();
} else {
  echo "connection succeeded <br>";
}

//Create table;
$lat = htmlspecialchars($_GET["lat"]);
$lng = htmlspecialchars($_GET["lng"]);
$id = htmlspecialchars($_GET["id"]);
$bearing = htmlspecialchars($_GET["bearing"]);

$sql="USE marauder_map;";
$update = "UPDATE marauder_map SET isMoving = true, bearing = " . $bearing . ", lat = " . $lat . ", lng = ". $lng . " where id=" . $id . ";";

mysqli_query($con, $sql);
mysqli_query($con, $update);

echo $sql;
echo $update;

mysqli_close($con);
?>

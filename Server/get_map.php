<?php
$con = mysqli_connect("54.165.53.129","root","blargHblargh1");
mysqli_query($con, "USE marauder_map;");
$result = mysqli_query($con, "SELECT * FROM marauder_map WHERE isMoving = true");
//Display json
$counter = 0;
$numresults = mysqli_num_rows($result);

echo "[";

while($row = mysqli_fetch_array($result)) {
  if (++$counter == $numresults) {
     echo "{\n". "id: " . $row['id'] . ",\nname: " . $row['name'] . ",\nlat:" . $row['lat'] . ",\nlng:" . $row['lng'] . ",\nisMoving: " . $row['isMoving'] . ",\nbearing:" . $row['bearing'] . "}\n";
  } else {
     echo "{\n" . "id: ". $row['id'] . ",\nname: " . $row['name'] . ",\nlat:" . $row['lat'] . ",\nlng:" . $row['lng'] . ",\nisMoving: " . $row['isMoving'] . ",\nbearing:" . $row['bearing'] . "},\n";
  }
}

echo "]";

mysqli_close($con);
?>

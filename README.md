For create jar:<br>
mvn clean compile assembly:single

Save image:<br>
docker save dailybotimage > dailybotimage.tar

Load image:<br>
docker load --input dailybotimage.tar

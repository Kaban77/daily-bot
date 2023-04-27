For create jar:
mvn clean compile assembly:single

Save image:
docker save dailybotimage > dailybotimage.tar

Load image:
docker load --input dailybotimage.tar

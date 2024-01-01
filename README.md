#### For create jar:
*mvn clean compile assembly:single*

#### Save docker image:
*docker save dailybotimage > dailybotimage.tar*

#### Load image:
*docker load --input dailybotimage.tar*

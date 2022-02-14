FROM openjdk:8-alpine

COPY target/uberjar/luminus_reagent_material_ui.jar /luminus_reagent_material_ui/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/luminus_reagent_material_ui/app.jar"]

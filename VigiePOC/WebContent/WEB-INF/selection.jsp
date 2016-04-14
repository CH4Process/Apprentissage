<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="jstl_core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Sélection des données</title>
        <link type="text/css" rel="stylesheet" href="form.css" />
    </head>
    <body>
        <form method="post" action="releves">
            <fieldset>
                <legend>Sélection</legend>
                <p>Vous pouvez sélectionner la source des données via ce formulaire.</p>

                <label for="capteur">Capteur :</label>
                <span class="erreur">${form.getErreur()}</span>
                <select name="capteurs">
                	<jstl_core:forEach items = "${listeCapteurs.getListe()}" var="capteurs">
                		<option value="${capteurs.key}">${capteurs.value[0]}</option>
                	</jstl_core:forEach>
                </select>
                <br />
                
                <input type="submit" value="Valider" class="sansLabel" />
                <br />
                
            </fieldset>
        </form>
    </body>
</html>
<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="jstl_core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>Affichage des données du MAXI IO de Christian</title>
        <link type="text/css" rel="stylesheet" href="form.css" />
    </head>
    <body>
        <form method="post" action="commandes">
            <fieldset>
                <legend>Etat des entrées et sorties</legend>
                <p>Vous pouvez visualiser l'état des entrées et sorties ainsi que piloter les sorties du module YoctoMaxiIO de Christian !.</p>

                <span class="erreur">Connexion au hub : ${module.erreurs['Hub']}</span>
                <br />
                
                <span class="erreur">Connexion au capteur de température : ${module.erreurs['Temperature']}</span>
                <br />
                
                <span class="erreur">Connexion au module MaxiIO : ${module.erreurs['DigitalIO']}</span>
                <br />
                
                <span class="erreur">Ecriture sur le module MaxiIO : ${module.erreurs['SetState']}</span>
                <br />
                
                <jstl_core:forEach items = "${module.channels}" var="canal">
                	CANAL :  ${canal.key}  -- TYPE :  ${canal.value}  -- ETAT :  ${module.channelsState[canal.key]}  
                	<jstl_core:if test="${canal.value == 'Output'}"> <input type="submit" name ="cmd_${canal.key}" value="Inverser" class="sansLabel" />  </jstl_core:if>
                	<br />
                </jstl_core:forEach>
            </fieldset>
        </form>
    </body>
</html>
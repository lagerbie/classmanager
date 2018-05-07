/*
 * ClassManager - Supervision de classes et Laboratoire de langue
 * Copyright (C) 2013 Fabrice Alleau <fabrice.alleau@siclic.fr>
 *
 * This file is part of ClassManager.
 *
 * ClassManager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * ClassManager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with ClassManager.  If not, see <http://www.gnu.org/licenses/>.
 */
package supervision;

/**
 * Définition d'une commande entre le noyau professeur et les miniatures de la
 * mosaïque.
 *
 * @author Fabrice Alleau
 * @version 1.90
 */
public class ProcessCommand {

    /**
     * Identifiant inconnu.
     */
    public static final String UNKNOWN = "unkonwn";
    /**
     * Action de fermeture.
     */
    public static final String CLOSE = "close";
    /**
     * Identifiant de la commande.
     */
    private String action;
    /**
     * Paramètre.
     */
    private int parameter;

    /**
     * Initialisation d'une commande avec paramètre.
     *
     * @param action l'identifiant de la commande.
     * @param parameter le parmètre.
     */
    public ProcessCommand(String action, int parameter) {
        this.action = action;
        this.parameter = parameter;
    }

    /**
     * Donne l'identifiant de la commande.
     *
     * @return l'identifiant de la commande.
     */
    public String getAction() {
        return action;
    }

    /**
     * Retourne la valeur du paramètre.
     *
     * @return la valeur du paramètre.
     */
    public int getParameter() {
        return parameter;
    }

    /**
     * Retourne la commande XML correspondante.
     *
     * @return la commande xml de la forme:
     *   <?xml version=\"1.0\" encoding=\"UTF-8\"?>
     *   <command>
     *     <surpervision> action
     *       <parameter> parameter </parameter>
     *     </surpervision>
     *   </command>
     */
    public String createXMLCommand() {
        String xmlEntete = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String xmlParameter = "<parameter>" + parameter + "</parameter>";
        String xmlAction = "<surpervision>" + action + xmlParameter + "</surpervision>";
        return xmlEntete + "<command>" + xmlAction + "</command>";
    }

    /**
     * Crée une commande avec une chaîne en XML.
     *
     * @param xml la chaîne en XML.
     * @return la commande correspondante.
     */
    public static ProcessCommand createCommand(String xml) {
        String xmlAction = UNKNOWN;
        int xmlParameter = 0;

        String[] split = xml.split(
                "<action>|</surpervision>|<parameter>|</parameter></surpervision>");

        if (split.length == 3) {
            xmlAction = split[1];
        } else if (split.length == 4) {
            xmlAction = split[1];
            xmlParameter = Integer.parseInt(split[2]);
        }

        return new ProcessCommand(xmlAction, xmlParameter);
    }
}

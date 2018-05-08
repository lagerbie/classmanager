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
package thot.labo.index;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Index d'insertion de fichier.
 *
 * @author Fabrice Alleau
 * @version 1.8.4
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IndexFile extends Index {

    /**
     * Nom du fichier associé.
     */
    private String fileName;

    /**
     * Initilise un index d'insetion de fichier sans temps initial.
     *
     * @param fileName le nom de fichier.
     */
    public IndexFile(String fileName) {
        this(fileName, -1);
    }

    /**
     * Initilise un index avec le temps initial.
     *
     * @param fileName le nom de fichier.
     * @param initialTime le temps initial.
     */
    public IndexFile(String fileName, long initialTime) {
        super(IndexType.FILE, initialTime);
        this.fileName = fileName;
    }

    @Override
    public void clean() {
        super.clean();
        this.fileName = null;
    }
}

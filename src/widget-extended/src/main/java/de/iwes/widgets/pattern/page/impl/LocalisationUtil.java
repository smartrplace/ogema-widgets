/**
 * This file is part of the OGEMA widgets framework.
 *
 * OGEMA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OGEMA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OGEMA. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2014 - 2018
 *
 * Fraunhofer-Gesellschaft zur Förderung der angewandten Wissenschaften e.V.
 *
 * Fraunhofer IWES/Fraunhofer IEE
 */

package de.iwes.widgets.pattern.page.impl;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

public class LocalisationUtil {
	
	public static String saveButtonText(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "�nderungen speichern";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Enregistrer les modifications";
		else	// default: English
			msg = "Save changes";
		return msg;
	}
	
	public static String deleteButtonText(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "L�schen";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Supprimer";
		else	// default: English
			msg = "Delete";
		return msg;
	}
	
	public static String cancelMsg(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Abbrechen";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Annuler";
		else	// default: English
			msg = "Cancel";
		return msg;
	}
	
	public static String deleteConfirmationPopupTitle(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "L�schen best�tigen";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Confirmation";
		else	// default: English
			msg = "Confirm deletion";
		return msg;
	}
	
	public static String deleteConfirmationMsg(String selected, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = selected + " wirklich l�schen?";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Supprimer " + selected + "?";
		else	// default: English
			msg = "Do you really want to delete " + selected + "?";
		return msg;
	}
	
	public static String deleteResource(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Ressource l�schen";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Annuler la ressource";
		else	// default: English
			msg = "Delete Resource";
		return msg;
	}
	
	public static String deletionConfirmed(String path, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Die Ressource " + path + " wurde gel�scht.";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "La ressource " + path + " a �t� supprim�e.";
		else	// default: English
			msg = "The resource " + path + " has been deleted.";
		return msg;
	}
	
	public static String deletionFailed(String path, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = path + " konnte nicht gel�scht werden";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Il n'�tait pas possible de supprimer " + path;
		else	// default: English
			msg = "Could not delete " + path;
		return msg;
	}
	
	public static String patternModified(String path, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Die �nderungen an " + path + " wurden gespeichert";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Les modifications de " + path + " ont �t� enregistr�es";
		else	// default: English
			msg = "Pattern has been modified: " + path;
		return msg;
	}
	
	public static String patternModificationFailed(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Die �nderungen konnten nicht gespeichert werden";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Il n'�tait pas possible d'enregistrer les modifications";
		else	// default: English
			msg = "The modifications could not be saved";
		return msg;
	}
	
	public static String typeSelectorLabel(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Typ ausw�hlen";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Choisis le type";
		else	// default: English
			msg = "Select type";
		return msg;
	}
	
	public static String parentSelectorLabel(String type, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Elternressource ausw�hlen<br>(Typ: " + type + ")";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Choisis le parent<br>(Type: " + type + ")";
		else	// default: English
			msg = "Select the parent Resource<br>(Type: " + type + ")";
		return msg;
	}
	
	public static String patternCreated(String path, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Pattern " + path + " wurde erzeugt" ;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Pattern " + path + " cr�e";
		else	// default: English
			msg = "Pattern " + path + " has been created";
		return msg;
	}
	
	public static String patternNotCreated(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Pattern konnte nicht angelegt werden" ;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Pattern pas cr�e";
		else	// default: English
			msg = "Pattern could not be created";
		return msg;
	}
	
	public static String create(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Anlegen" ;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Cr�er";
		else	// default: English
			msg = "Create";
		return msg;
	}
	
	public static String resourceExistsMessage(String newResName, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Ressource " + newResName + " existiert bereits, und konnte nicht angelegt werden" ;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "La Ressource " + newResName + " existe, et ne peut pas etre cr�e";
		else	// default: English
			msg = "Could not create resource " + newResName + ", already exists";
		return msg;
	}
	
	public static String selectParentMessage(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Bitte eine Elternressource w�hlen." ;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Choisi le parent.";
		else	// default: English
			msg = "Please select the parent Resource first.";
		return msg;
	}
	
	public static String topResource(OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Neuer Ressourcenname";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Ressource principal (nom)";
		else	// default: English
			msg = "New resource name";
		return msg;
	}
	
	public static String filterMsgNotAFloatingPointNr(String id, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Bitte geben Sie eine Flie�kommazahl im Feld " + id + " ein.";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Please enter a floating point number in field " + id; // trop compliqu�
		else	// default: English
			msg = "Please enter a floating point number in field " + id;
		return msg;
	}

	public static String filterMsgNotAnInteger(String id, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Bitte geben Sie eine ganze Zahl im Feld " + id + " ein.";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Please enter an integer number in field " + id; // trop compliqu�
		else	// default: English
			msg = "Please enter an integer number in field " + id;
		return msg;
	}
	
	public static String filterMsgNotAHexValue(String id, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Bitte geben Sie einen g�ltigen Hexadezimal-Wert im Feld " + id + " ein.";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Please enter a valid hexadecimal value in field " + id; // trop compliqu�
		else	// default: English
			msg = "Please enter a valid hexadecimal value in field " + id;
		return msg;
	}
	
	public static String filterMsgExternal(String id, String externalFilter, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Der Wert im Feld " + id + " muss dem regul�ren Ausdruck " + externalFilter + " entsprechen";
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Value in field " + id + " must match the regular expression " + externalFilter; // trop compliqu�
		else	// default: English
			msg = "Value in field " + id + " must match the regular expression " + externalFilter;
		return msg;
	}
	
	public static String errorSettingValue(String id, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Ein Patter-Wert konnte nicht gesetzt werden: " + id;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Il n'�tait pas possible d'enregistrer les modifications pour " + id;
		else	// default: English
			msg = "A pattern value could not be set: " + id;
		return msg;
	}
	
	public static String invalidResourceName(String resourceName, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Ung�ltiger Ressourcenname: " + resourceName;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Invalid resource name: " + resourceName;
		else	// default: English
			msg ="Invalid resource name: "+ resourceName;
		return msg;
	}
	
	public static String valueTooLarge(String id, float actualValue, float upperBound, OgemaLocale locale) {
		return valueTooLarge(id, String.valueOf(actualValue), String.valueOf(upperBound), locale);
	}
	
	public static String valueTooLarge(String id, long actualValue, long upperBound, OgemaLocale locale) {
		return valueTooLarge(id, String.valueOf(actualValue), String.valueOf(upperBound), locale);
	}
	
	public static String valueTooLarge(String id, float actualValue, long upperBound, OgemaLocale locale) {
		return valueTooLarge(id, String.valueOf(actualValue), String.valueOf(upperBound), locale);
	}
	
	public static String valueTooLarge(String id, String actualValue, String upperBound, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Der Wert " + actualValue + " im Feld " + id + " ist gr��er als der h�chste zul�ssige Wert " + upperBound;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Value " + actualValue + " in field " + id + " exceeds allowed maximum value " + upperBound; // trop compliqu�
		else	// default: English
			msg = "Value " + actualValue + " in field " + id + " exceeds allowed maximum value " + upperBound; 
		return msg;
	}
	
	public static String valueTooSmall(String id, float actualValue, float lowerBound, OgemaLocale locale) {
		return valueTooSmall(id, String.valueOf(actualValue), String.valueOf(lowerBound), locale);
	}
	
	public static String valueTooSmall(String id, long actualValue, long lowerBound, OgemaLocale locale) {
		return valueTooSmall(id, String.valueOf(actualValue), String.valueOf(lowerBound), locale);
	}
	
	public static String valueTooSmall(String id, float actualValue, long lowerBound, OgemaLocale locale) {
		return valueTooSmall(id, String.valueOf(actualValue), String.valueOf(lowerBound), locale);
	}
	
	public static String valueTooSmall(String id, String actualValue, String lowerBound, OgemaLocale locale) {
		if (locale == null) locale = OgemaLocale.ENGLISH;
		String msg;
		if (locale.equals(OgemaLocale.GERMAN)) 
			msg = "Der Wert " + actualValue + " im Feld " + id + " ist kleiner als der Minimalwert " + lowerBound;
		else if (locale.equals(OgemaLocale.FRENCH))
			msg = "Value " + actualValue + " in field " + id + " is too small. Minimum value: " + lowerBound; // trop compliqu�
		else	// default: English
			msg = "Value " + actualValue + " in field " + id + " is too small. Minimum value: " + lowerBound; 
		return msg;
	}
}

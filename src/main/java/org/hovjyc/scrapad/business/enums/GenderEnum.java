package org.hovjyc.scrapad.business.enums;

/**
 * Gender.
 */
public enum GenderEnum {
    /** The available genders. */
    HOMME("homme"), FEMME("femme"), COUPLE("couple");

    /** The string gender. */
    private String genderStr = "";

    /**
     * Constructor.
     * @param pGenderStr
     *            The gender
     */
    GenderEnum(final String pGenderStr) {
        this.genderStr = pGenderStr;
    }

    /**
     * Get the enum corresponding to the string.
     * @param pGenderStr
     *            The gender in String format.
     * @return The gender in enum format.
     */
    public static GenderEnum fromString(final String pGenderStr) {
        for (GenderEnum lGender : GenderEnum.values()) {
            if (lGender.genderStr.equalsIgnoreCase(pGenderStr)) {
                return lGender;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return genderStr;
    }
}

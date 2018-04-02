package org.hovjyc.scrapad;

/**
 * Gender.
 */
public enum Gender {
    /** The available genders. */
    HOMME("homme"), FEMME("femme"), COUPLE("couple");

    /** The string gender. */
    private String genderStr = "";

    /**
     * Constructor.
     * @param pGenderStr
     *            The gender
     */
    Gender(final String pGenderStr) {
        this.genderStr = pGenderStr;
    }

    /**
     * Get the enum corresponding to the string.
     * @param pGenderStr
     *            The gender in String format.
     * @return The gender in enum format.
     */
    public static Gender fromString(final String pGenderStr) {
        for (Gender lGender : Gender.values()) {
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

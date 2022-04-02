package forTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {
    public static void main(String[] args) throws ParseException {
        String testString = "a@gmail.com";
        String result = stringObstractor(testString);
        System.out.println(result);
    }

    public static String ordinalConverter(Integer num) {
        // If tens place is "1", it uses "th".
        // Otherwise, it uses "xx1st", "xx2nd", "xx3rd" or "xxxth"

        String numString = num.toString();
        Integer length = numString.length();

        if (length < 2) {
            switch (numString) {
                case "1":
                    return numString += "st";
                case "2":
                    return numString += "nd";
                case "3":
                    return numString += "rd";
                default:
                    return numString += "th";
            }
        }

        // If tens place is "1", it uses "th".
        if (numString.substring(length - 2, length - 1).equals("1")) {
            return numString += "th";
        }

        switch (numString.substring(length - 1, length)) {
            case "1":
                return numString += "st";
            case "2":
                return numString += "nd";
            case "3":
                return numString += "rd";
            default:
                return numString += "th";
        }
    }

    public static Integer sundayCounter(String date_from, String date_to) throws ParseException {
        // If you start the calculation from Monday, 
        // the number of weeks will match the number of Sundays.
        // If "date_from" is not Monday, 
        // this function advance the day to the next Monday and add a Sunday count.

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date dateFrom = sdf.parse(date_from);
        Date dateTo = sdf.parse(date_to);
        Integer counter = 0;
        int DIV_FOR_CAL = 1000 * 60 * 60 * 24;

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dateFrom);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(dateTo);
        int day = cal1.get(Calendar.DAY_OF_WEEK);

        // If it is not Monday, add counter and advance the date to the next Monday
        // Reference: 1: Sunday 2: Monday 3: Tuesday... 7:Saturday
        switch (day) {
            case 1:
                cal1.add(Calendar.DAY_OF_MONTH, 0);
                counter++;
                break;
            case 3:
                cal1.add(Calendar.DAY_OF_MONTH, 5);
                counter++;
                break;
            case 4:
                cal1.add(Calendar.DAY_OF_MONTH, 4);
                counter++;
                break;
            case 5:
                cal1.add(Calendar.DAY_OF_MONTH, 3);
                counter++;
                break;
            case 6:
                cal1.add(Calendar.DAY_OF_MONTH, 2);
                counter++;
                break;
            case 7:
                cal1.add(Calendar.DAY_OF_MONTH, 1);
                counter++;
                break;
            default:
                cal1.add(Calendar.DAY_OF_MONTH, -1);
                break;
        }

        // Calculate the number of weeks. It matches number of Sundays.
        // e.g. 16 days = 2 weeks + 2 days (Monday, Tuesday) -> 2 Sundays
        int weeks = (int) ((cal2.getTimeInMillis() - cal1.getTimeInMillis()) / DIV_FOR_CAL) / 7;

        return counter += weeks;
    }

    public static String stringObstractor(String originalString) {
        if (originalString.contains("@")) {
            return mailObstractor(originalString);
        } else {
            return phoneObstrocotr(originalString);
        }
    }

    public static String mailObstractor(String originalString) {
        // This pattern is not allowed " " although these are allowed in RFC 5322.
        // Since it can be cause of the SQL injection and normally it is not used
        // (+ it make code too complicated), do not allow it this time.
        String regPart = "[\\w!#\\$%&'\\*\\+\\-/=\\?\\^_`\\{\\|\\}~]";
        String dotAtom = regPart + "+(\\." + regPart + "+)*";
        String regExp = "^" + dotAtom + "@" + dotAtom + "$";

        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(originalString);

        // Validate the composition of the mail address.
        if (!matcher.find())
            return "Please enter correct E-mail or phone numbers.";

        // Convert address to lowercase and obstract local part
        String obstractedString = originalString.toLowerCase();
        String localPart = obstractedString.split("@")[0];
        String domainPart = obstractedString.split("@")[1];
        Integer localLength = localPart.length();

        // If length is 1, we can consider more what is best for user.
        // localPart = "*"; also seems to good.
        if (localLength == 1) {
            localPart += "*****";
        } else {
            localPart = localPart.substring(0, 1) + "*****" + localPart.substring(localLength - 1, localLength);
        }

        obstractedString = localPart + "@" + domainPart;

        return obstractedString;
    }

    public static String phoneObstrocotr(String originalString) {
        // This does not check the position of the space since I do not aware of all
        // country phone number rules.
        // e.g. "+81-12-3456-7-8-9" seems to be strange, but it is allowed in this time.
        // We can check it with regexp, especially using "{n}"
        String regExp = "^[\\+0-9]+[\\s0-9]*[0-9]$";
        String consecSpace = "\\s\\s";

        // Check for consecutive spaces
        Pattern pattern = Pattern.compile(consecSpace);
        Matcher matcher = pattern.matcher(originalString);
        if (matcher.find())
            return "Please enter correct E-mail or phone numbers";

        // Check phone number rules
        pattern = Pattern.compile(regExp);
        matcher = pattern.matcher(originalString);
        if (!matcher.find())
            return "Please enter correct E-mail or phone numbers";

        // Check the length of phne numbers.
        // According to Wikipedia, maximum length of phone number seems to be 15
        Integer phoneNums = originalString.replaceAll("\\s|\\+", "").length();
        if (phoneNums < 9 || phoneNums > 15)
            return "Please enter correct E-mail or phone numbers";

        // Convert " " to "-"
        String obstractedString = originalString.replaceAll("\\s", "-");
        // Get the border of the string to be converted
        pattern = Pattern.compile("[0-9]");
        matcher = pattern.matcher(obstractedString);
        for (int i = 0; i < phoneNums - 4; i++) {
            matcher.find();
        }

        // matcher.start() shows the boader. Before boader, it should be converted.
        obstractedString = obstractedString.substring(0, matcher.start()).replaceAll("[0-9]", "*")
                + obstractedString.substring(matcher.end());

        return obstractedString;
    }
}

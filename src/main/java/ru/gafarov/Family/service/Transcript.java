package ru.gafarov.Family.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class Transcript {
    private final String[] Cyrillic = {"а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о", "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я"};
    private final String[] latin = {"a", "b", "v", "g", "d", "e", "jo", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "c", "ch", "sh", "shh", "", "y", "'", "e", "ju", "ja"};
    private final List<String> CyrillicList = Arrays.asList(Cyrillic);
    private final List<String> latinList = Arrays.asList(latin);

    public String toCyrillic(String partOfName) {
        String partOfNameLowerCase = partOfName.toLowerCase();
        int i = 0;
        StringBuilder partOfNameCyrillicLowerCase = new StringBuilder();
        while (i < partOfNameLowerCase.length()) {
            String threeSymbols = "";
            String twoSymbols = "";
            String oneSymbol = partOfNameLowerCase.substring(i, i + 1);
            if (partOfNameLowerCase.substring(i).length() > 2) {
                threeSymbols = partOfNameLowerCase.substring(i, i + 3);
            }
            if (partOfNameLowerCase.substring(i).length() > 1) {
                twoSymbols = partOfNameLowerCase.substring(i, i + 2);
            }
            if (threeSymbols.equals("shh")) {
                partOfNameCyrillicLowerCase.append("щ");
                i += 3;
            } else {
                switch (twoSymbols) {
                    case "jo": partOfNameCyrillicLowerCase.append("ё"); i += 2; break;
                    case "zh": partOfNameCyrillicLowerCase.append("ж"); i += 2; break;
                    case "ch": partOfNameCyrillicLowerCase.append("ч"); i += 2; break;
                    case "sh": partOfNameCyrillicLowerCase.append("ш"); i += 2; break;
                    case "ju": partOfNameCyrillicLowerCase.append("ю"); i += 2; break;
                    case "ja": partOfNameCyrillicLowerCase.append("я"); i += 2; break;
                    default:
                        if (latinList.contains(oneSymbol)){
                            partOfNameCyrillicLowerCase.append(Cyrillic[latinList.indexOf(oneSymbol)]);
                        } else {
                            partOfNameCyrillicLowerCase.append(oneSymbol);
                        }
                        i++;
                        break;
                }
            }
        }

        return partOfNameCyrillicLowerCase.toString();
    }


    public String toLatin(String partOfName) {
        String partOfNameLowerCase = partOfName.toLowerCase();
        int i = 0;
        StringBuilder partOfNameLatinLowerCase = new StringBuilder();
        while (i < partOfNameLowerCase.length()) {
            String oneSymbol = partOfNameLowerCase.substring(i, i + 1);

            if (CyrillicList.contains(oneSymbol)) {
                partOfNameLatinLowerCase.append(latin[CyrillicList.indexOf(oneSymbol)]);
            } else {
                partOfNameLatinLowerCase.append(oneSymbol);
            }
            i++;
        }
        return partOfNameLatinLowerCase.toString();
    }
    public String trimSoftAndHardSymbol(String str){
        return str.replaceAll("[ьъ']","");
    }
}

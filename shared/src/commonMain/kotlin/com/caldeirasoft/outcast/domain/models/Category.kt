package com.caldeirasoft.outcast.domain.models

import kotlinx.serialization.Serializable

/**
 * Supported category types encountered within the `<itunes:category>` element
 * within a `<channel>` element, modeled as a finite set sealed class.
 *
 * Defined category values are listed in
 * [Apple Podcasts Categories](https://help.apple.com/itc/podcasts_connect/#/itc9267a2f12).
 * The categories and their nested hierarchies are modeled according to the table below.
 * This classes [companion object][Factory] exposed a reference for each instance.
 *
 * | Category                | Subcategory        | Property                    |
 * |-------------------------|--------------------|-----------------------------|
 * | Arts                    | –                  | [ARTS]                      |
 * | Arts                    | Books              | [BOOKS]                     |
 * | Arts                    | Design             | [DESIGN]                    |
 * | Arts                    | Fashion & Beauty   | [FASHION_AND_BEAUTY]        |
 * | Arts                    | Food               | [FOOD]                      |
 * | Arts                    | Performing Arts    | [PERFORMING_ARTS]           |
 * | Arts                    | Visual Arts        | [VISUAL_ARTS]               |
 * | Business                | –                  | [BUSINESS]                  |
 * | Business                | Careers            | [CAREERS]                   |
 * | Business                | Entrepreneurship   | [ENTREPRENEURSHIP]          |
 * | Business                | Investing          | [INVESTING]                 |
 * | Business                | Management         | [MANAGEMENT]                |
 * | Business                | Marketing          | [MARKETING]                 |
 * | Business                | Non-Profit         | [NON_PROFIT]                |
 * | Comedy                  | –                  | [COMEDY]                    |
 * | Comedy                  | Comedy Interviews  | [COMEDY_INTERVIEWS]         |
 * | Comedy                  | Improv             | [IMPROV]                    |
 * | Comedy                  | Stand-Up           | [STAND_UP]                  |
 * | Education               | –                  | [EDUCATION]                 |
 * | Education               | Courses            | [COURSES]                   |
 * | Education               | How To             | [HOW_TO]                    |
 * | Education               | Language Learning  | [LANGUAGE_LEARNING]         |
 * | Education               | Self-Improvement   | [SELF_IMPROVEMENT]          |
 * | Fiction                 | –                  | [FICTION]                   |
 * | Fiction                 | Comedy Fiction     | [COMEDY_FICTION]            |
 * | Fiction                 | Drama              | [DRAMA]                     |
 * | Fiction                 | Science Fiction    | [SCIENCE_FICTION]           |
 * | Government              | –                  | [GOVERNMENT]                |
 * | History                 | –                  | [HISTORY]                   |
 * | Health & Fitness        | –                  | [HEALTH_AND_FITNESS]        |
 * | Health & Fitness        | Alternative Health | [ALTERNATIVE_HEALTH]        |
 * | Health & Fitness        | Fitness            | [FITNESS]                   |
 * | Health & Fitness        | Medicine           | [MEDICINE]                  |
 * | Health & Fitness        | Mental Health      | [MENTAL_HEALTH]             |
 * | Health & Fitness        | Nutrition          | [NUTRITION]                 |
 * | Health & Fitness        | Sexuality          | [SEXUALITY]                 |
 * | Kids & Family           | –                  | [KIDS_AND_FAMILY]           |
 * | Kids & Family           | Education for Kids | [EDUCATION_FOR_KIDS]        |
 * | Kids & Family           | Parenting          | [PARENTING]                 |
 * | Kids & Family           | Pets & Animals     | [PETS_AND_ANIMALS]          |
 * | Kids & Family           | Stories for Kids   | [STORIES_FOR_KIDS]          |
 * | Leisure                 | –                  | [LEISURE]                   |
 * | Leisure                 | Animation & Manga  | [ANIMATION_AND_MANGA]       |
 * | Leisure                 | Automotive         | [AUTOMOTIVE]                |
 * | Leisure                 | Aviation           | [AVIATION]                  |
 * | Leisure                 | Crafts             | [CRAFTS]                    |
 * | Leisure                 | Games              | [GAMES]                     |
 * | Leisure                 | Hobbies            | [HOBBIES]                   |
 * | Leisure                 | Home & Garden      | [HOME_AND_GARDEN]           |
 * | Leisure                 | Video Games        | [VIDEO_GAMES]               |
 * | Music                   | –                  | [MUSIC]                     |
 * | Music                   | Music Commentary   | [MUSIC_COMMENTARY]          |
 * | Music                   | Music History      | [MUSIC_HISTORY]             |
 * | Music                   | Music Interviews   | [MUSIC_INTERVIEWS]          |
 * | News                    | –                  | [NEWS]                      |
 * | News                    | Business News      | [BUSINESS_NEWS]             |
 * | News                    | Daily News         | [DAILY_NEWS]                |
 * | News                    | Entertainment News | [ENTERTAINMENT_NEWS]        |
 * | News                    | News Commentary    | [NEWS_COMMENTARY]           |
 * | News                    | Politics           | [POLITICS]                  |
 * | News                    | Sports News        | [SPORTS_NEWS]               |
 * | News                    | Tech News          | [TECH_NEWS]                 |
 * | Religion & Spirituality | –                  | [RELIGION_AND_SPIRITUALITY] |
 * | Religion & Spirituality | Buddhism           | [BUDDHISM]                  |
 * | Religion & Spirituality | Christianity       | [CHRISTIANITY]              |
 * | Religion & Spirituality | Hinduism           | [HINDUISM]                  |
 * | Religion & Spirituality | Islam              | [ISLAM]                     |
 * | Religion & Spirituality | Judaism            | [JUDAISM]                   |
 * | Religion & Spirituality | Religion           | [RELIGION]                  |
 * | Religion & Spirituality | Spirituality       | [SPIRITUALITY]              |
 * | Science                 | –                  | [SCIENCE]                   |
 * | Science                 | Astronomy          | [ASTRONOMY]                 |
 * | Science                 | Chemistry          | [CHEMISTRY]                 |
 * | Science                 | Earth Sciences     | [EARTH_SCIENCES]            |
 * | Science                 | Life Sciences      | [LIFE_SCIENCES]             |
 * | Science                 | Mathematics        | [MATHEMATICS]               |
 * | Science                 | Natural Sciences   | [NATURAL_SCIENCES]          |
 * | Science                 | Nature             | [NATURE]                    |
 * | Science                 | Physics            | [PHYSICS]                   |
 * | Science                 | Social Sciences    | [SOCIAL_SCIENCES]           |
 * | Society & Culture       | –                  | [SOCIETY_AND_CULTURE]       |
 * | Society & Culture       | Documentary        | [DOCUMENTARY]               |
 * | Society & Culture       | Personal Journals  | [PERSONAL_JOURNALS]         |
 * | Society & Culture       | Philosophy         | [PHILOSOPHY]                |
 * | Society & Culture       | Places & Travel    | [PLACES_AND_TRAVEL]         |
 * | Society & Culture       | Relationships      | [RELATIONSHIPS]             |
 * | Sports                  | –                  | [SPORTS]                    |
 * | Sports                  | Baseball           | [BASEBALL]                  |
 * | Sports                  | Basketball         | [BASKETBALL]                |
 * | Sports                  | Cricket            | [CRICKET]                   |
 * | Sports                  | Fantasy Sports     | [FANTASY_SPORTS]            |
 * | Sports                  | Football           | [FOOTBALL]                  |
 * | Sports                  | Golf               | [GOLF]                      |
 * | Sports                  | Hockey             | [HOCKEY]                    |
 * | Sports                  | Rugby              | [RUGBY]                     |
 * | Sports                  | Running            | [RUNNING]                   |
 * | Sports                  | Soccer             | [SOCCER]                    |
 * | Sports                  | Swimming           | [SWIMMING]                  |
 * | Sports                  | Tennis             | [TENNIS]                    |
 * | Sports                  | Volleyball         | [VOLLEYBALL]                |
 * | Sports                  | Wilderness         | [WILDERNESS]                |
 * | Sports                  | Wrestling          | [WRESTLING]                 |
 * | Technology              | –                  | [TECHNOLOGY]                |
 * | True Crime              | –                  | [TRUE_CRIME]                |
 * | TV & Film               | –                  | [TV_AND_FILM]               |
 * | TV & Film               | After Shows        | [AFTER_SHOWS]               |
 * | TV & Film               | Film History       | [FILM_HISTORY]              |
 * | TV & Film               | Film Interviews    | [FILM_INTERVIEWS]           |
 * | TV & Film               | Film Reviews       | [FILM_REVIEWS]              |
 * | TV & Film               | TV Reviews         | [TV_REVIEWS]                |
 */

@Serializable
enum class Category(
    val text: String,
    val id: Int,
    val nested: Boolean = false,
    val parent: Category? = null,
) {
    ARTS("Arts", 1301),
    FOOD("Food", 1306, true, ARTS),
    DESIGN("Design", 1402, true, ARTS),
    PERFORMING_ARTS("Performing Arts", 1405, true, ARTS),
    VISUAL_ARTS("Visual Arts", 1406, true, ARTS),
    FASHION_AND_BEAUTY("Fashion & Beauty", 1459, true, ARTS),
    BOOKS("Books", 1482, true, ARTS),

    BUSINESS("Business", 1321),
    CAREERS("Careers", 1410, true, BUSINESS),
    INVESTING("Investing", 1412, true, BUSINESS),
    MANAGEMENT("Management", 1491, true, BUSINESS),
    MARKETING("Marketing", 1492, true, BUSINESS),
    ENTREPRENEURSHIP("Entrepreneurship", 1493, true, BUSINESS),
    NON_PROFIT("Non-Profit", 1494, true, BUSINESS),

    COMEDY("Comedy", 1303),
    IMPROV("Improv", 1495, true, COMEDY),
    COMEDY_INTERVIEWS("Comedy Interviews", 1496, true, COMEDY),
    STAND_UP("Stand-Up", 1497, true, COMEDY),

    EDUCATION("Education", 1304),
    LANGUAGE_LEARNING("Language Learning", 1498, true, EDUCATION),
    HOW_TO("How To", 1499, true, EDUCATION),
    SELF_IMPROVEMENT("Self-Improvement", 1500, true, EDUCATION),
    COURSES("Courses", 1501, true, EDUCATION),

    FICTION("Fiction", 1483),
    DRAMA("Drama", 1484, true, FICTION),
    SCIENCE_FICTION("Science Fiction", 1485, true, FICTION),
    COMEDY_FICTION("Comedy Fiction", 1486, true, FICTION),

    GOVERNMENT("Government", 1511),

    HEALTH_AND_FITNESS("Health & Fitness", 1512),
    ALTERNATIVE_HEALTH("Alternative Health", 1513, true, HEALTH_AND_FITNESS),
    FITNESS("Fitness", 1514, true, HEALTH_AND_FITNESS),
    NUTRITION("Nutrition", 1515, true, HEALTH_AND_FITNESS),
    SEXUALITY("Sexuality", 1516, true, HEALTH_AND_FITNESS),
    MENTAL_HEALTH("Mental Health", 1517, true, HEALTH_AND_FITNESS),
    MEDICINE("Medicine", 1518, true, HEALTH_AND_FITNESS),

    HISTORY("History", 1487),

    KIDS_AND_FAMILY("Kids & Family", 1305),
    EDUCATION_FOR_KIDS("Education for Kids", 1519, true, KIDS_AND_FAMILY),
    STORIES_FOR_KIDS("Stories for Kids", 1520, true, KIDS_AND_FAMILY),
    PARENTING("Parenting", 1521, true, KIDS_AND_FAMILY),
    PETS_AND_ANIMALS("Pets & Animals", 1522, true, KIDS_AND_FAMILY),

    LEISURE("Leisure", 1502),
    AUTOMOTIVE("Automotive", 1503, true, LEISURE),
    AVIATION("Aviation", 1504, true, LEISURE),
    Hobbies("Hobbies", 1505, true, LEISURE),
    CRAFTS("Crafts", 1506, true, LEISURE),
    GAMES("Games", 1507, true, LEISURE),
    HOME_AND_GARDEN("Home & Garden", 1508, true, LEISURE),
    VIDEO_GAMES("Video Games", 1509, true, LEISURE),
    ANIMATION_AND_MANGA("Animation & Manga", 1510, true, LEISURE),

    MUSIC("Music", 1310),
    MUSIC_COMMENTARY("Music Commentary", 1523, true, MUSIC),
    MUSIC_HISTORY("Music History", 1524, true, MUSIC),
    MUSIC_INTERVIEWS("Music Interviews", 1525, true, MUSIC),

    NEWS("News", 1489),
    BUSINESS_NEWS("Business News", 1490, true, NEWS),
    DAILY_NEWS("Daily News", 1526, true, NEWS),
    POLITICS("Politics", 1527, true, NEWS),
    TECH_NEWS("Tech News", 1528, true, NEWS),
    SPORTS_NEWS("Sports News", 1529, true, NEWS),
    NEWS_COMMENTARY("News Commentary", 1530, true, NEWS),
    ENTERTAINMENT_NEWS("Entertainment News", 1531, true, NEWS),

    RELIGION_AND_SPIRITUALITY("Religion & Spirituality", 1314),
    BUDDHISM("Buddhism", 1438, true, RELIGION_AND_SPIRITUALITY),
    CHRISTIANITY("Christianity", 1439, true, RELIGION_AND_SPIRITUALITY),
    ISLAM("Islam", 1440, true, RELIGION_AND_SPIRITUALITY),
    JUDAISM("Judaism", 1441, true, RELIGION_AND_SPIRITUALITY),
    SPIRITUALITY("Spirituality", 1444, true, RELIGION_AND_SPIRITUALITY),
    HINDUISM("Hinduism", 1463, true, RELIGION_AND_SPIRITUALITY),
    RELIGION("Religion", 1532, true, RELIGION_AND_SPIRITUALITY),

    SCIENCE("Science", 1533),
    NATURAL_SCIENCES("Natural Sciences", 1534, true, SCIENCE),
    SOCIAL_SCIENCES("Social Sciences", 1535, true, SCIENCE),
    MATHEMATICS("Mathematics", 1536, true, SCIENCE),
    NATURE("Nature", 1537, true, SCIENCE),
    ASTRONOMY("Astronomy", 1538, true, SCIENCE),
    CHEMISTRY("Chemistry", 1539, true, SCIENCE),
    EARTH_SCIENCES("Earth Sciences", 1540, true, SCIENCE),
    LIFE_SCIENCES("Life Sciences", 1541, true, SCIENCE),
    PHYSICS("Physics", 1542, true, SCIENCE),

    SOCIETY_AND_CULTURE("Society & Culture", 1324),
    PERSONAL_JOURNALS("Personal Journals", 1302, true, SOCIETY_AND_CULTURE),
    PLACES_AND_TRAVEL("Places & Travel", 1320, true, SOCIETY_AND_CULTURE),
    PHILOSOPHY("Philosophy", 1443, true, SOCIETY_AND_CULTURE),
    DOCUMENTARY("Documentary", 1543, true, SOCIETY_AND_CULTURE),
    RELATIONSHIPS("Relationships", 1544, true, SOCIETY_AND_CULTURE),

    SPORTS("Sports", 1545),
    SOCCER("Soccer", 1546, true, SPORTS),
    FOOTBALL("Football", 1547, true, SPORTS),
    BASKETBALL("Basketball", 1548, true, SPORTS),
    BASEBALL("Baseball", 1549, true, SPORTS),
    HOCKEY("Hockey", 1550, true, SPORTS),
    RUNNING("Running", 1551, true, SPORTS),
    RUGBY("Rugby", 1552, true, SPORTS),
    GOLF("Golf", 1553, true, SPORTS),
    CRICKET("Cricket", 1554, true, SPORTS),
    WRESTLING("Wrestling", 1555, true, SPORTS),
    TENNIS("Tennis", 1556, true, SPORTS),
    VOLLEYBALL("Volleyball", 1557, true, SPORTS),
    SWIMMING("Swimming", 1558, true, SPORTS),
    WILDERNESS("Wilderness", 1559, true, SPORTS),
    FANTASY_SPORTS("Fantasy Sports", 1560, true, SPORTS),

    TECHNOLOGY("Technology", 1318),

    TRUE_CRIME("True Crime", 1488),

    TV_AND_FILM("TV & Film", 1309),
    TV_REVIEWS("TV Reviews", 1561, true, TV_AND_FILM),
    AFTER_SHOWS("After Shows", 1562, true, TV_AND_FILM),
    FILM_REVIEWS("Film Reviews", 1563, true, TV_AND_FILM),
    FILM_HISTORY("Film History", 1564, true, TV_AND_FILM),
    FILM_INTERVIEWS("Film Interviews", 1565, true, TV_AND_FILM);

    companion object {
        fun fromName(rawValue: String): Category? =
            values().find { t -> t.text.equals(rawValue, ignoreCase = true) }

        fun fromId(id: Int): Category? =
            values().find { t -> t.id == id }
    }
}
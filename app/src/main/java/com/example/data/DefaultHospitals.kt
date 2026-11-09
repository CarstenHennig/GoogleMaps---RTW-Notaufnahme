package com.example.data

import com.example.model.BedStatus
import com.example.model.CareLevel
import com.example.model.EmergencySpecialty
import com.example.model.Hospital

object DefaultHospitals {
  val list: List<Hospital> = listOf(
    // BERLIN
    Hospital(
      id = "charite_mitte",
      name = "Charité - Universitätsmedizin Berlin (Campus Mitte)",
      emergencyDepartmentName = "Zentrale Notaufnahme (ZNA Mitte)",
      latitude = 52.5255,
      longitude = 13.3778,
      address = "Charitéplatz 1",
      city = "Berlin",
      postalCode = "10117",
      phone = "+49 30 45050",
      emergencyPhone = "+49 30 450553012",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.NEURO,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "ZNA regulär aufnahmebereit",
      hasHeliport = false
    ),
    Hospital(
      id = "charite_cvk",
      name = "Charité Campus Virchow-Klinikum",
      emergencyDepartmentName = "Notaufnahme Campus Virchow (Schockraum)",
      latitude = 52.5422,
      longitude = 13.3444,
      address = "Augustenburger Platz 1",
      city = "Berlin",
      postalCode = "13353",
      phone = "+49 30 45050",
      emergencyPhone = "+49 30 450553200",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.BURN,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Kinder-Notaufnahme & Schockraum 24/7",
      hasHeliport = true
    ),
    Hospital(
      id = "ukb_berlin",
      name = "BG Klinikum Unfallkrankenhaus Berlin (UKB)",
      emergencyDepartmentName = "Überregionales Traumazentrum & ZNA",
      latitude = 52.5186,
      longitude = 13.5768,
      address = "Warener Str. 7",
      city = "Berlin",
      postalCode = "12683",
      phone = "+49 30 56810",
      emergencyPhone = "+49 30 56811234",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.BURN,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.CPU,
        EmergencySpecialty.NEURO
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Schwerstverbranntenzentrum & Handtrauma 24/7 aktiv",
      hasHeliport = true
    ),
    Hospital(
      id = "vivantes_friedrichshain",
      name = "Vivantes Klinikum im Friedrichshain",
      emergencyDepartmentName = "Rettungsstelle Friedrichshain",
      latitude = 52.5249,
      longitude = 13.4354,
      address = "Landsberger Allee 49",
      city = "Berlin",
      postalCode = "10249",
      phone = "+49 30 130230",
      emergencyPhone = "+49 30 130231600",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Aufnahmebereit",
      hasHeliport = true
    ),
    Hospital(
      id = "vivantes_neukoelln",
      name = "Vivantes Klinikum Neukölln",
      emergencyDepartmentName = "Zentrale Notaufnahme Neukölln",
      latitude = 52.4411,
      longitude = 13.4447,
      address = "Rudower Str. 48",
      city = "Berlin",
      postalCode = "12351",
      phone = "+49 30 130140",
      emergencyPhone = "+49 30 130142200",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.RESTRICTED,
      statusNote = "Hohe Belegung, Schockraum frei",
      hasHeliport = true
    ),

    // MÜNCHEN
    Hospital(
      id = "lmu_grosshadern",
      name = "LMU Klinikum Großhadern",
      emergencyDepartmentName = "Notaufnahme Großhadern (Schockraum)",
      latitude = 48.1114,
      longitude = 11.4703,
      address = "Marchioninistr. 15",
      city = "München",
      postalCode = "81377",
      phone = "+49 89 44000",
      emergencyPhone = "+49 89 440073000",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.NEURO,
        EmergencySpecialty.BURN
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Überregionales Traumazentrum & HKL 24h",
      hasHeliport = true
    ),
    Hospital(
      id = "tum_rechts_der_isar",
      name = "Klinikum rechts der Isar (TUM)",
      emergencyDepartmentName = "Interdisziplinäre Notaufnahme (INA)",
      latitude = 48.1362,
      longitude = 11.6018,
      address = "Ismaninger Str. 22",
      city = "München",
      postalCode = "81675",
      phone = "+49 89 41400",
      emergencyPhone = "+49 89 41402222",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.NEURO,
        EmergencySpecialty.BURN
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Herzkatheter & Stroke bereit",
      hasHeliport = true
    ),
    Hospital(
      id = "muenchen_schwabing",
      name = "München Klinik Schwabing",
      emergencyDepartmentName = "Notfallzentrum & Kinder-Notaufnahme",
      latitude = 48.1728,
      longitude = 11.5794,
      address = "Kölner Platz 1",
      city = "München",
      postalCode = "80804",
      phone = "+49 89 30680",
      emergencyPhone = "+49 89 30682600",
      level = CareLevel.SCHWERPUNKT,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.CPU,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Große Kinder-Notaufnahme 24/7",
      hasHeliport = false
    ),
    Hospital(
      id = "muenchen_bogenhausen",
      name = "München Klinik Bogenhausen",
      emergencyDepartmentName = "Zentrum für Notfallmedizin Bogenhausen",
      latitude = 48.1566,
      longitude = 11.6288,
      address = "Englschalkinger Str. 77",
      city = "München",
      postalCode = "81925",
      phone = "+49 89 92700",
      emergencyPhone = "+49 89 92702121",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.BURN
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Verbrennungszentrum & Stroke aktiv",
      hasHeliport = true
    ),

    // HAMBURG
    Hospital(
      id = "uke_hamburg",
      name = "Universitätsklinikum Hamburg-Eppendorf (UKE)",
      emergencyDepartmentName = "Zentrale Notaufnahme UKE",
      latitude = 53.5919,
      longitude = 9.9744,
      address = "Martinistr. 52",
      city = "Hamburg",
      postalCode = "20246",
      phone = "+49 40 74100",
      emergencyPhone = "+49 40 741053000",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.NEURO,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Überregionales Traumazentrum & ZNA 24/7",
      hasHeliport = true
    ),
    Hospital(
      id = "asklepios_st_georg",
      name = "Asklepios Klinik St. Georg",
      emergencyDepartmentName = "Zentrale Notaufnahme St. Georg",
      latitude = 53.5594,
      longitude = 10.0211,
      address = "Lohmühlenstraße 5",
      city = "Hamburg",
      postalCode = "20099",
      phone = "+49 40 1818850",
      emergencyPhone = "+49 40 1818852222",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.BURN
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Schwerstverbranntenzentrum Nord & Herzkatheter",
      hasHeliport = true
    ),
    Hospital(
      id = "asklepios_altona",
      name = "Asklepios Klinik Altona",
      emergencyDepartmentName = "ZNA Altona",
      latitude = 53.5552,
      longitude = 9.9075,
      address = "Paul-Ehrlich-Straße 1",
      city = "Hamburg",
      postalCode = "22763",
      phone = "+49 40 1818810",
      emergencyPhone = "+49 40 1818811111",
      level = CareLevel.SCHWERPUNKT,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Regulär geöffnet",
      hasHeliport = true
    ),

    // FRANKFURT AM MAIN
    Hospital(
      id = "uni_frankfurt",
      name = "Universitätsklinikum Frankfurt",
      emergencyDepartmentName = "Zentrale Notaufnahme (ZNA Haus 23)",
      latitude = 50.0933,
      longitude = 8.6599,
      address = "Theodor-Stern-Kai 7",
      city = "Frankfurt am Main",
      postalCode = "60590",
      phone = "+49 69 63010",
      emergencyPhone = "+49 69 63015112",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.NEURO
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Polytrauma-Schockraum & CPU aufnahmebereit",
      hasHeliport = true
    ),
    Hospital(
      id = "bg_unfall_frankfurt",
      name = "BG Unfallklinik Frankfurt am Main (BGU)",
      emergencyDepartmentName = "Notaufnahme & Schockraum BGU",
      latitude = 50.1558,
      longitude = 8.6972,
      address = "Friedberger Landstraße 430",
      city = "Frankfurt am Main",
      postalCode = "60389",
      phone = "+49 69 4750",
      emergencyPhone = "+49 69 4752222",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.BURN,
        EmergencySpecialty.NEURO
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Überregionales Traumazentrum & Hubschrauberlandeplatz",
      hasHeliport = true
    ),
    Hospital(
      id = "nordwest_frankfurt",
      name = "Krankenhaus Nordwest",
      emergencyDepartmentName = "Zentrale Notaufnahme Nordwest",
      latitude = 50.1578,
      longitude = 8.6186,
      address = "Steinbacher Hohl 2-26",
      city = "Frankfurt am Main",
      postalCode = "60488",
      phone = "+49 69 76011",
      emergencyPhone = "+49 69 76013222",
      level = CareLevel.SCHWERPUNKT,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.CPU
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Stroke Unit zertifiziert",
      hasHeliport = false
    ),

    // KÖLN & BONN
    Hospital(
      id = "uni_koeln",
      name = "Uniklinik Köln",
      emergencyDepartmentName = "Zentrale Notaufnahme (Gebäude 26)",
      latitude = 50.9255,
      longitude = 6.9189,
      address = "Kerpener Str. 62",
      city = "Köln",
      postalCode = "50937",
      phone = "+49 221 4780",
      emergencyPhone = "+49 221 4788888",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.NEURO,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Maximalversorger 24/7 betriebsbereit",
      hasHeliport = true
    ),
    Hospital(
      id = "kliniken_koeln_merheim",
      name = "Krankenhaus Köln-Merheim",
      emergencyDepartmentName = "Notfallzentrum & Schockraum Merheim",
      latitude = 50.9458,
      longitude = 7.0422,
      address = "Ostmerheimer Str. 200",
      city = "Köln",
      postalCode = "51109",
      phone = "+49 221 89070",
      emergencyPhone = "+49 221 89073333",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.BURN,
        EmergencySpecialty.NEURO,
        EmergencySpecialty.CPU
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Schwerstbrandverletztenzentrum NRW",
      hasHeliport = true
    ),
    Hospital(
      id = "uni_bonn",
      name = "Universitätsklinikum Bonn (UKB)",
      emergencyDepartmentName = "Notfallzentrum Venusberg",
      latitude = 50.7025,
      longitude = 7.0986,
      address = "Venusberg-Campus 1",
      city = "Bonn",
      postalCode = "53127",
      phone = "+49 228 2870",
      emergencyPhone = "+49 228 28712000",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Überregionales Traumazentrum Bonn",
      hasHeliport = true
    ),

    // STUTTGART
    Hospital(
      id = "klinikum_stuttgart_katharinen",
      name = "Klinikum Stuttgart - Katharinenhospital",
      emergencyDepartmentName = "Interdisziplinäres Notfallzentrum (INZ)",
      latitude = 48.7845,
      longitude = 9.1722,
      address = "Kriegsbergstraße 60",
      city = "Stuttgart",
      postalCode = "70174",
      phone = "+49 711 27801",
      emergencyPhone = "+49 711 27830303",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.NEURO
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Schockraum & Katheterlabor 24/7 besetzt",
      hasHeliport = true
    ),
    Hospital(
      id = "klinikum_stuttgart_olgahospital",
      name = "Klinikum Stuttgart - Olgahospital (Olgäle)",
      emergencyDepartmentName = "Pädiatrische Notaufnahme & Kinder-Schockraum",
      latitude = 48.7865,
      longitude = 9.1705,
      address = "Kriegsbergstraße 62",
      city = "Stuttgart",
      postalCode = "70174",
      phone = "+49 711 27804",
      emergencyPhone = "+49 711 27872470",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PERINATAL
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Spezialisiert auf Neugeborene, Kinder & Jugendliche",
      hasHeliport = true
    ),

    // NÜRNBERG & ERLANGEN
    Hospital(
      id = "klinikum_nuernberg_sued",
      name = "Klinikum Nürnberg Süd",
      emergencyDepartmentName = "Notfallzentrum Klinikum Süd",
      latitude = 49.4061,
      longitude = 11.1189,
      address = "Breslauer Str. 201",
      city = "Nürnberg",
      postalCode = "90471",
      phone = "+49 911 3980",
      emergencyPhone = "+49 911 3982440",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.BURN,
        EmergencySpecialty.PEDIATRIC
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Maximalversorger Nordbayern & Schockraum",
      hasHeliport = true
    ),

    // DRESDEN & LEIPZIG
    Hospital(
      id = "uni_dresden",
      name = "Universitätsklinikum Carl Gustav Carus Dresden",
      emergencyDepartmentName = "Universitäres Notfallzentrum (UNZ)",
      latitude = 51.0569,
      longitude = 13.7806,
      address = "Fetscherstraße 74",
      city = "Dresden",
      postalCode = "01307",
      phone = "+49 351 4580",
      emergencyPhone = "+49 351 4582222",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.NEURO
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "UNZ & Hubschrauberlandeplatz einsatzbereit",
      hasHeliport = true
    ),
    Hospital(
      id = "uni_leipzig",
      name = "Universitätsklinikum Leipzig",
      emergencyDepartmentName = "Zentrale Notfallaufnahme (ZNA Liebigstraße)",
      latitude = 51.3308,
      longitude = 12.3855,
      address = "Liebigstraße 20",
      city = "Leipzig",
      postalCode = "04103",
      phone = "+49 341 97109",
      emergencyPhone = "+49 341 9717800",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "24h Schockraum & Herzkatheter",
      hasHeliport = true
    ),

    // HANNOVER
    Hospital(
      id = "mhh_hannover",
      name = "Medizinische Hochschule Hannover (MHH)",
      emergencyDepartmentName = "Notaufnahme MHH (Gebäude K6)",
      latitude = 52.3828,
      longitude = 9.8039,
      address = "Carl-Neuberg-Straße 1",
      city = "Hannover",
      postalCode = "30625",
      phone = "+49 511 5320",
      emergencyPhone = "+49 511 5322052",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.BURN,
        EmergencySpecialty.NEURO
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Überregionales Traumazentrum & Schockraum",
      hasHeliport = true
    ),

    // RUHRGEBIET (Dortmund / Essen)
    Hospital(
      id = "uni_essen",
      name = "Universitätsklinikum Essen",
      emergencyDepartmentName = "Notaufnahme Operatives Zentrum",
      latitude = 51.4332,
      longitude = 6.9922,
      address = "Hufelandstraße 55",
      city = "Essen",
      postalCode = "45147",
      phone = "+49 201 7230",
      emergencyPhone = "+49 201 7231200",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC,
        EmergencySpecialty.NEURO
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Schockraum & Katheter 24h",
      hasHeliport = true
    ),
    Hospital(
      id = "klinikum_dortmund",
      name = "Klinikum Dortmund Mitte",
      emergencyDepartmentName = "Zentrale Notaufnahme Beurhausstraße",
      latitude = 51.5072,
      longitude = 7.4528,
      address = "Beurhausstraße 40",
      city = "Dortmund",
      postalCode = "44137",
      phone = "+49 231 9530",
      emergencyPhone = "+49 231 95321300",
      level = CareLevel.MAXIMALVERSORGUNG,
      specialties = setOf(
        EmergencySpecialty.ZNA,
        EmergencySpecialty.CPU,
        EmergencySpecialty.STROKE,
        EmergencySpecialty.TRAUMA,
        EmergencySpecialty.PEDIATRIC
      ),
      status = BedStatus.AVAILABLE,
      statusNote = "Große Notfallaufnahme & Hubschrauberlandeplatz",
      hasHeliport = true
    )
  )
}

# **Planer podróży**

Aplikacja mobilna stworzona w Kotlinie, umożliwiająca planowanie i organizację podróży. Użytkownik może tworzyć wyjazdy, planować atrakcje na poszczególne dni, wyświetlać miejsca na mapie oraz prowadzić dziennik podróży.

Celem projektu było stworzenie aplikacji wspomagającej organizację podróży poprzez zebranie wszystkich najważniejszych informacji w jednym miejscu.

### Aplikacja umożliwia:
- tworzenie podróży,
- edycje i usuwanie podróży,
- planowanie atrakcji dla poszczególnych dni,
- wyszukiwanie miejsc z wykorzystaniem Geoapify API,
- prezentację atrakcji na mapie,
- przechowywanie danych lokalnie,
- prowadzenie dziennika podróży.

## Funkcjonalności
### Zarządzanie podróżami
- Dodawanie podróży
- Edycja podróży
- Usuwanie podróży
- Wyświetlanie listy podróży
  
### Planowanie atrakcji
Użytkownik może dodawać atrakcje do wybranego dnia podróży.
Dla każdej atrakcji można zapisać:
- nazwę,
- lokalizację,
- kategorię,
- godzinę,
- opis.

### Integracja z API

Aplikacja wykorzystuje Geoapify API do:
- autouzupełniania lokalizacji,
- geokodowania adresów,
- wyszukiwania atrakcji,
- pobierania współrzędnych geograficznych.

### Mapa
Przy użyciu biblioteki MapLibre użytkownik może:
- wyświetlać zapisane miejsca na mapie,
- przeglądać lokalizację podróży,
- wyświetlać znaczniki atrakcji,
- oglądać trasę pomiędzy punktami.

### Dziennik podróży
Dla każdego dnia podróży użytkownik może:
- dodawać własne notatki,
- przechowywać wspomnienia z wyjazdu.

## Autor
Projekt wykonany w ramach projektu semestralnego z zajęć "Projekt aplikacji mobilnej 1" przez **Emilię Biesiadę**. 

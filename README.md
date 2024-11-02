# The app

A modern android application which allows users to browse Pokemons from PokeAPI in a list and
to search a Pokemon by name.

## Features

- **Browse and Search**: Users can browse the list of pokemons and search for a specific pokemon by name.
- **Pagination**: The list loads dynamically with 20 Pokemons per page fetching from API and Room.
- **Offline Access**: Fetched Pokemons are stored in a Room database so it can be used offline.
This allows also less API calls and faster loading of Pokemons on startup.

## Technologies

- **Kotlin**
- **Jetpack Compose**
- **Koin**
- **Room**
- **Retrofit**
- **Coil**
- **PokeAPI**

## Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/AaroKoinsaari/pokemon-box.git
    ```

2. Open the project in Android Studio.

3. Build the project and run it on an emulator or a physical device.

## Disclaimer

This project was developed as part of a technical assessment for a certain company. This project and its
code is not licensed for any commercial or distribution purposes. A report of the licenses for the
used libraries can be found in the `LICENSES.md` file.

## Contact

If you have any inquiries, please contact me via email at [aaro.koinsaari@proton.me](mailto:aaro.koinsaari@proton.me)

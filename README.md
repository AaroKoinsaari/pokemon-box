# Pokemon Box

This is a modern Android application that lets users browse Pokemons from PokeAPI. The purpose of this project is to demonstrate the use of modern Android technologies in building a simple, yet efficient Android application.

## Features

- **Browse and Search**: Users can browse the list of Pokemons and search for a specific Pokemon by name.
- **Pagination**: The list loads dynamically with 20 Pokemons per page fetching from the API and Room.
- **Offline Access**: Fetched Pokemons are stored in a Room database so they can be used offline. This allows also less API calls and faster loading of Pokemons on startup.

## Technologies

- **Kotlin**
- **Jetpack Compose**
- **Koin**
- **Room**
- **Retrofit**
- **Coil**
- **PokeAPI**

**_NOTE_**: _The app handles pagination manually, although the Paging 3 library could have been used as it is specifically designed for this purpose. It would provide a more efficient and scalable way for loading paginated data, and its use might be a better approach in larger or more complex applications. However, for simplicity and demonstration purposes, Paging 3 was not used in this project._

## Installation

### Development Version

1. Clone the repository:

   ```bash
   git clone https://github.com/AaroKoinsaari/pokemon-box.git
   ```

2. Open the project in Android Studio.

3. Build the project and run it on an emulator or a physical device.

### Release Version

1. Download the latest APK from the [Releases page](https://github.com/AaroKoinsaari/pokemon-box/releases).

2. On your Android device, enable the installation for unknown apps.

3. Open the downloaded APK file and follow the on-screen instructions to install the app.

## License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details. For information about third-party libraries used in this project and their licenses, see the [LICENSES](./LICENSES.md) file.

## Disclaimer

This application uses PokeAPI for data, which has its own rate limits and usage policies. The developer of this app is not responsible for any misuse of the app, including excessive API requests that may violate PokeAPI's fair use policy. Users are encouraged to use the app responsibly and avoid actions that could negatively impact PokeAPI's availability for others. For more details about the API usage policies, please refer to the [PokeAPI Fair Use Policy](https://pokeapi.co/docs/v2#fairuse).

## Contact

For any inquiries, feel free to contact me via email at [aaro.koinsaari@proton.me](mailto:aaro.koinsaari@proton.me) or connect in LinkedIn: [aarokoinsaari](https://www.linkedin.com/in/AaroKoinsaari).

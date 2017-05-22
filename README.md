# GoMint

GoMint is a Minecraft PocketEdition server implementation that is still work in progress. The goal is
to provide a full, modifiable server with plugin support for Java.

The project is divided into four modules:

##### gomint-api
The API for plugin developers. This is currently not stable and will not be developed in this stage
of the project. It will be finalized once all core components inside the server work.

##### gomint-server
This is the implementation of the server. Fiddling with this is not supported but we will help you if we can.
This part of the project is developing the fastest. If you want to contribute to the software watch this module the
most.

##### gomint-helper
Holds generator helpers for all type of things. Currently this only holds a class generator for Blocks.

##### gomint-testplugin
A plugin to test and see the API design. 

## Contact & Social
[![Join the Discord](http://puu.sh/v9UB9/944431c790.png)](https://discord.gg/qC4nJVN)
[![Twitter](http://puu.sh/v9V9H/ad70c8acf7.png)](https://twitter.com/GomintPe)

## Todo
* Implement all Tile Entity types
* Implement all block interfaces for the API
* Implement basic events for Movement and join / leave

## Testing

As the project is still work in progress the only way to launch the application currently is to set up
your respective IDE and provide a working directory that contains a regular Minecraft PC Anvil World.
Afterwards you may join the server using your version of MC:PE.

## License

The code found in this repository is licensed under a 3-clause BSD license. See the LICENSE file for further
details.

## Documentation

There currently is no documentation as things are still changing frequently.

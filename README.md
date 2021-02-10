# mclogs-bukkit
A bukkit plugin to easily share and analyse your server logs with [mclo.gs](https://mclo.gs)


### commands:
    /mclogs
Upload your current log to mclogs (Permission: mclogs.upload)
    
    /mclogs list
List all available log files (Permission: mclogs.list)

    /mclogs share <filename>
Share a specific log file (Permission: mclogs.share)

### Developing
This mod uses the [mclogs-java](https://github.com/aternosorg/mclogs-java) library.
You need to run the following command to add it to the project:
`git submodule init && git submodule update`
package com.tynellis.World.world_parts;

import com.tynellis.World.Tiles.Tile;
import com.tynellis.World.world_parts.Regions.Region;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

public class Layer implements Land, Serializable {
    private Tile[][] tiles = new Tile[Area.WIDTH][Area.HEIGHT];

    public Layer(Random rand) {
    }

    public void updateTileArt(Layer[][] adjacentLayers){
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                Tile[][] adjacent = new Tile[3][3];
                for (int h = 0; h <= 2; h++) {
                    for(int g = 0; g <= 2; g++){
                        int jg = j + g-1;
                        int ih = i + h-1;
                        if (jg >= 0 && ih >= 0 && jg < Area.WIDTH && ih < Area.HEIGHT){
                            adjacent[g][h] = tiles[jg][ih];
                        }
                        if((jg == -1 && ih == -1 || jg == Area.WIDTH && ih == Area.HEIGHT || jg == -1 && ih == Area.HEIGHT || jg == Area.WIDTH && ih == -1) && adjacentLayers[(int)(1 + (jg*1.0)/ Area.WIDTH)][(int)(1 + (ih*1.0)/ Area.HEIGHT)] != null){
                            Layer layer = adjacentLayers[(int)(1 + (jg*1.0)/ Area.WIDTH)][(int)(1 + (ih*1.0)/ Area.HEIGHT)];
                            if (ih < 0) {
                                ih += Area.HEIGHT;
                            } else if(ih >= Area.HEIGHT) {
                                ih -= Area.HEIGHT;
                            }
                            if (jg < 0) {
                                jg += Area.WIDTH;
                            } else if(jg >= Area.WIDTH) {
                                jg -= Area.WIDTH;
                            }
                            adjacent[g][h] = layer.getTile(jg, ih);
                        } else if (jg == Area.WIDTH && adjacentLayers[2][1 + ih/ Area.HEIGHT] != null) {
                            Layer layer = adjacentLayers[2][1 + ih/ Area.HEIGHT];
                            if (ih < 0) {
                                ih += Area.HEIGHT;
                            } else if(ih >= Area.HEIGHT) {
                                ih -= Area.HEIGHT;
                            }
                            adjacent[g][h] = layer.getTile(0, ih);
                        } else if (jg == -1 && adjacentLayers[0][1 + ih/ Area.HEIGHT] != null) {
                            Layer layer = adjacentLayers[0][1 + ih/ Area.HEIGHT];
                            if (ih < 0) {
                                ih += Area.HEIGHT;
                            } else if(ih >= Area.HEIGHT) {
                                ih -= Area.HEIGHT;
                            }
                            adjacent[g][h] = layer.getTile(Area.WIDTH - 1, ih);
                        } else if (ih == Area.HEIGHT && adjacentLayers[1 + jg/ Area.WIDTH][2] != null) {
                            Layer layer = adjacentLayers[1 + jg/ Area.WIDTH][2];
                            if (jg < 0) {
                                jg += Area.WIDTH;
                            } else if(jg >= Area.WIDTH) {
                                jg -= Area.WIDTH;
                            }
                            adjacent[g][h] = layer.getTile(jg, 0);
                        } else if (ih == -1 && adjacentLayers[1 + jg/ Area.WIDTH][0] != null) {
                            Layer layer = adjacentLayers[1 + jg/ Area.WIDTH][0];
                            if (jg < 0) {
                                jg += Area.WIDTH;
                            } else if(jg >= Area.WIDTH) {
                                jg -= Area.WIDTH;
                            }
                            adjacent[g][h] = layer.getTile(jg, Area.HEIGHT - 1);
                        }
                    }
                }
                if (tiles[j][i] != null) {
                    tiles[j][i].updateArt(adjacent);
                }
            }
        }
    }

    public void updateTiles(Region region, Layer[][] adjacentLayers, int X, int Y, int Z, Random rand) {
        int maxUpdates = 16;
        int numOfUpdates = 0;

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (maxUpdates > numOfUpdates && rand.nextInt(tiles.length * tiles[i].length) < maxUpdates) {
                    Tile[][] adjacent = new Tile[3][3];
                    for (int h = 0; h <= 2; h++) {
                        for (int g = 0; g <= 2; g++) {
                            int jg = j + g - 1;
                            int ih = i + h - 1;
                            if (jg >= 0 && ih >= 0 && jg < Area.WIDTH && ih < Area.HEIGHT) {
                                adjacent[g][h] = tiles[jg][ih];
                            }
                            if ((jg == -1 && ih == -1 || jg == Area.WIDTH && ih == Area.HEIGHT || jg == -1 && ih == Area.HEIGHT || jg == Area.WIDTH && ih == -1) && adjacentLayers[(int) (1 + (jg * 1.0) / Area.WIDTH)][(int) (1 + (ih * 1.0) / Area.HEIGHT)] != null) {
                                Layer layer = adjacentLayers[(int) (1 + (jg * 1.0) / Area.WIDTH)][(int) (1 + (ih * 1.0) / Area.HEIGHT)];
                                if (ih < 0) {
                                    ih += Area.HEIGHT;
                                } else if (ih >= Area.HEIGHT) {
                                    ih -= Area.HEIGHT;
                                }
                                if (jg < 0) {
                                    jg += Area.WIDTH;
                                } else if (jg >= Area.WIDTH) {
                                    jg -= Area.WIDTH;
                                }
                                adjacent[g][h] = layer.getTile(jg, ih);
                            } else if (jg == Area.WIDTH && adjacentLayers[2][1 + ih / Area.HEIGHT] != null) {
                                Layer layer = adjacentLayers[2][1 + ih / Area.HEIGHT];
                                if (ih < 0) {
                                    ih += Area.HEIGHT;
                                } else if (ih >= Area.HEIGHT) {
                                    ih -= Area.HEIGHT;
                                }
                                adjacent[g][h] = layer.getTile(0, ih);
                            } else if (jg == -1 && adjacentLayers[0][1 + ih / Area.HEIGHT] != null) {
                                Layer layer = adjacentLayers[0][1 + ih / Area.HEIGHT];
                                if (ih < 0) {
                                    ih += Area.HEIGHT;
                                } else if (ih >= Area.HEIGHT) {
                                    ih -= Area.HEIGHT;
                                }
                                adjacent[g][h] = layer.getTile(Area.WIDTH - 1, ih);
                            } else if (ih == Area.HEIGHT && adjacentLayers[1 + jg / Area.WIDTH][2] != null) {
                                Layer layer = adjacentLayers[1 + jg / Area.WIDTH][2];
                                if (jg < 0) {
                                    jg += Area.WIDTH;
                                } else if (jg >= Area.WIDTH) {
                                    jg -= Area.WIDTH;
                                }
                                adjacent[g][h] = layer.getTile(jg, 0);
                            } else if (ih == -1 && adjacentLayers[1 + jg / Area.WIDTH][0] != null) {
                                Layer layer = adjacentLayers[1 + jg / Area.WIDTH][0];
                                if (jg < 0) {
                                    jg += Area.WIDTH;
                                } else if (jg >= Area.WIDTH) {
                                    jg -= Area.WIDTH;
                                }
                                adjacent[g][h] = layer.getTile(jg, Area.HEIGHT - 1);
                            }
                        }
                    }
                    if (tiles[j][i] != null) {
                        tiles[j][i].update(region, adjacent, (X * Area.WIDTH) + j, (Y * Area.HEIGHT) + i, Z, rand);
                        numOfUpdates++;
                    }
                }
            }
        }
    }

    public void renderRow(int row, Graphics g, int xOffset, int yOffset) {
        for (int w = 0; w < tiles.length; w++) {
            if (tiles[w][row] != null) {
                tiles[w][row].render(g, w * Tile.WIDTH + xOffset, row * Tile.HEIGHT + yOffset);
            }
        }
    }

    public void renderRowTop(int row, Graphics g, int xOffset, int yOffset) {
        for (int w = 0; w < tiles.length; w++) {
            if (tiles[w][row] != null) {
                tiles[w][row].renderTop(g, w * Tile.WIDTH + xOffset, row * Tile.HEIGHT + yOffset);
            }
        }
    }

    public void render(Graphics g, int xOffset, int yOffset) {
        for (int h = 0; h < tiles[0].length; h++) {
            for (int w = 0; w < tiles.length; w++) {
                if (tiles[w][h] != null) {
                    tiles[w][h].render(g, w * Tile.WIDTH + xOffset, h * Tile.HEIGHT + yOffset);
                }
            }
        }
    }

    public void renderTop(Graphics g, int xOffset, int yOffset) {
        for (int h = 0; h < tiles[0].length; h++) {
            for (int w = 0; w < tiles.length; w++) {
                if (tiles[w][h] != null) {
                    tiles[w][h].renderTop(g, w * Tile.WIDTH + xOffset, h * Tile.HEIGHT + yOffset);
                }
            }
        }
    }

    public Tile getTile(int X, int Y) {
        return tiles[X][Y];
    }


    public void setTile(Tile tile, int x, int y) {
        tiles[x][y] = tile;

    }

    @Override
    public Tile getTile(int X, int Y, int Z) {
        return getTile(X,Y);
    }

    @Override
    public void setTile(Tile tile, int X, int Y, int Z) {
        setTile(tile, X, Y);
    }
}

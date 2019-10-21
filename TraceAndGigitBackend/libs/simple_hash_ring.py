import binascii


class HashRing(object):
    def __init__(self, values):
        self.values = values
        self.values_length = len(values)

    def get_node(self, key):
        return self.values[binascii.crc32(key) % self.values_length]


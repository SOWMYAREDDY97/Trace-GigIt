class InvalidParameterError(Exception):
    def __init__(self, parameter_name):
        self.parameter_name = parameter_name
        
    def __unicode__(self):
        return "Invalid parameter %s" + self.parameter_name
